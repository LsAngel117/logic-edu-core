package com.logossystemsit.logiceducore.interfaces.rest.academic.enrollment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logossystemsit.logiceducore.application.academic.enrollment.dto.command.EnrollStudentCommand;
import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.*;
import com.logossystemsit.logiceducore.interfaces.rest.academic.enrollment.dto.request.EnrollStudentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnrollmentController")
class EnrollmentControllerTest {

    private MockMvc mockMvc;

    @Mock private EnrollStudentUseCase enrollUseCase;
    @Mock private GetEnrollmentUseCase getUseCase;
    @Mock private ListEnrollmentsByGroupUseCase listUseCase;
    @Mock private DropEnrollmentUseCase dropUseCase;

    @InjectMocks
    private EnrollmentController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Instant NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String ENR_ID = "enr-001";
    private static final String USER_ID = "990e8400-e29b-41d4-a716-446655440004";
    private static final String GROUP_ID = "771e8400-e29b-41d4-a716-446655440005";

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private EnrollmentResult buildResult(String id, String status) {
        return new EnrollmentResult(id, USER_ID, GROUP_ID, status, NOW, NOW);
    }

    // ======================== POST /api/v1/enrollments ========================

    @Nested
    @DisplayName("POST /api/v1/enrollments")
    class EnrollStudent {

        @Test
        @DisplayName("should return 201 with EnrollmentResponse")
        void shouldReturn201WithEnrollmentResponse() throws Exception {
            EnrollmentResult result = buildResult(ENR_ID, "ACTIVE");
            when(enrollUseCase.execute(any(EnrollStudentCommand.class))).thenReturn(result);

            String body = """
                    {
                        "userId": "990e8400-e29b-41d4-a716-446655440004",
                        "groupId": "771e8400-e29b-41d4-a716-446655440005"
                    }
                    """;

            mockMvc.perform(post("/api/v1/enrollments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(ENR_ID))
                    .andExpect(jsonPath("$.userId").value(USER_ID))
                    .andExpect(jsonPath("$.groupId").value(GROUP_ID))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should return 409 on duplicate enrollment")
        void shouldReturn409OnDuplicateEnrollment() throws Exception {
            when(enrollUseCase.execute(any(EnrollStudentCommand.class)))
                    .thenThrow(new IllegalStateException("Student already enrolled in this group"));

            String body = """
                    {"userId": "990e8400-e29b-41d4-a716-446655440004", "groupId": "771e8400-e29b-41d4-a716-446655440005"}
                    """;

            mockMvc.perform(post("/api/v1/enrollments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("should return 422 on capacity full")
        void shouldReturn422OnCapacityFull() throws Exception {
            when(enrollUseCase.execute(any(EnrollStudentCommand.class)))
                    .thenThrow(new IllegalStateException("Group has reached maximum capacity"));

            String body = """
                    {"userId": "990e8400-e29b-41d4-a716-446655440004", "groupId": "771e8400-e29b-41d4-a716-446655440005"}
                    """;

            mockMvc.perform(post("/api/v1/enrollments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 422 on group inactive")
        void shouldReturn422OnGroupInactive() throws Exception {
            when(enrollUseCase.execute(any(EnrollStudentCommand.class)))
                    .thenThrow(new IllegalStateException("Group is inactive"));

            String body = """
                    {"userId": "990e8400-e29b-41d4-a716-446655440004", "groupId": "771e8400-e29b-41d4-a716-446655440005"}
                    """;

            mockMvc.perform(post("/api/v1/enrollments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 422 on group not found")
        void shouldReturn422OnGroupNotFound() throws Exception {
            when(enrollUseCase.execute(any(EnrollStudentCommand.class)))
                    .thenThrow(new IllegalArgumentException("Group not found"));

            String body = """
                    {"userId": "990e8400-e29b-41d4-a716-446655440004", "groupId": "771e8400-e29b-41d4-a716-446655440005"}
                    """;

            mockMvc.perform(post("/api/v1/enrollments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 409 on optimistic lock conflict")
        void shouldReturn409OnOptimisticLockConflict() throws Exception {
            when(enrollUseCase.execute(any(EnrollStudentCommand.class)))
                    .thenThrow(new org.springframework.dao.OptimisticLockingFailureException("Group capacity changed"));

            String body = """
                    {"userId": "990e8400-e29b-41d4-a716-446655440004", "groupId": "771e8400-e29b-41d4-a716-446655440005"}
                    """;

            mockMvc.perform(post("/api/v1/enrollments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }
    }

    // ======================== GET /api/v1/enrollments/{id} ========================

    @Nested
    @DisplayName("GET /api/v1/enrollments/{id}")
    class GetEnrollment {

        @Test
        @DisplayName("should return 200 with EnrollmentResponse")
        void shouldReturn200WithEnrollmentResponse() throws Exception {
            EnrollmentResult result = buildResult(ENR_ID, "ACTIVE");
            when(getUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(get("/api/v1/enrollments/{id}", ENR_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ENR_ID))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(getUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("Enrollment not found"));

            mockMvc.perform(get("/api/v1/enrollments/{id}", ENR_ID))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== GET /api/v1/groups/{groupId}/enrollments ========================

    @Nested
    @DisplayName("GET /api/v1/groups/{groupId}/enrollments")
    class ListEnrollmentsByGroup {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() throws Exception {
            EnrollmentResult r1 = buildResult("enr-001", "ACTIVE");
            EnrollmentResult r2 = buildResult("enr-002", "ACTIVE");
            when(listUseCase.execute(any())).thenReturn(List.of(r1, r2));

            mockMvc.perform(get("/api/v1/groups/{groupId}/enrollments", GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value("enr-001"))
                    .andExpect(jsonPath("$[1].id").value("enr-002"));
        }

        @Test
        @DisplayName("should return 200 with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(listUseCase.execute(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/groups/{groupId}/enrollments", GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // ======================== PATCH /api/v1/enrollments/{id}/drop ========================

    @Nested
    @DisplayName("PATCH /api/v1/enrollments/{id}/drop")
    class DropEnrollment {

        @Test
        @DisplayName("should return 200 with DROPPED status")
        void shouldReturn200WithDroppedStatus() throws Exception {
            EnrollmentResult result = buildResult(ENR_ID, "DROPPED");
            when(dropUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(patch("/api/v1/enrollments/{id}/drop", ENR_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ENR_ID))
                    .andExpect(jsonPath("$.status").value("DROPPED"));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(dropUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("Enrollment not found"));

            mockMvc.perform(patch("/api/v1/enrollments/{id}/drop", ENR_ID))
                    .andExpect(status().isNotFound());
        }
    }
}
