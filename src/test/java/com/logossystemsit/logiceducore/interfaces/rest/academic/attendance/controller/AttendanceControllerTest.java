package com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.RegisterAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.UpdateAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.application.academic.attendance.port.in.*;
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

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceController")
class AttendanceControllerTest {

    private MockMvc mockMvc;

    @Mock private RegisterAttendanceUseCase registerUseCase;
    @Mock private GetAttendanceByDateUseCase getByDateUseCase;
    @Mock private ListAttendancesByGroupUseCase listUseCase;
    @Mock private UpdateAttendanceUseCase updateUseCase;

    @InjectMocks
    private AttendanceController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Instant NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String GROUP_ID = "aaa00001-e29b-41d4-a716-446655440001";
    private static final String ATTENDANCE_ID = "att-001";
    private static final String STUDENT_ID = "990e8400-e29b-41d4-a716-446655440004";
    private static final String TEACHER_ID = "990e8400-e29b-41d4-a716-446655440004";
    private static final LocalDate DATE = LocalDate.of(2026, 6, 1);

    private final Principal mockPrincipal = () -> TEACHER_ID;

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private AttendanceResult buildResult(String status, String observations) {
        return new AttendanceResult(
                ATTENDANCE_ID, GROUP_ID, STUDENT_ID, DATE,
                status, observations, NOW, NOW
        );
    }

    // ======================== POST create ========================

    @Nested
    @DisplayName("POST /api/v1/groups/{groupId}/attendances")
    class RegisterAttendance {

        @Test
        @DisplayName("should return 201 with AttendanceResponse")
        void shouldReturn201() throws Exception {
            AttendanceResult result = buildResult("PRESENT", null);
            when(registerUseCase.execute(any(RegisterAttendanceCommand.class))).thenReturn(result);

            String body = """
                    {
                        "studentId": "990e8400-e29b-41d4-a716-446655440004",
                        "date": "2026-06-01",
                        "status": "PRESENT",
                        "observations": null
                    }
                    """;

            mockMvc.perform(post("/api/v1/groups/{groupId}/attendances", GROUP_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(ATTENDANCE_ID))
                    .andExpect(jsonPath("$.status").value("PRESENT"))
                    .andExpect(jsonPath("$.studentId").value(STUDENT_ID));
        }

        @Test
        @DisplayName("should return 422 when service throws IllegalArgumentException")
        void shouldReturn422() throws Exception {
            when(registerUseCase.execute(any(RegisterAttendanceCommand.class)))
                    .thenThrow(new IllegalArgumentException("Group not found"));

            String body = """
                    {
                        "studentId": "990e8400-e29b-41d4-a716-446655440004",
                        "date": "2026-06-01",
                        "status": "PRESENT"
                    }
                    """;

            mockMvc.perform(post("/api/v1/groups/{groupId}/attendances", GROUP_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 422 when teacher is not authorized")
        void shouldReturn422WhenNotAuthorized() throws Exception {
            when(registerUseCase.execute(any(RegisterAttendanceCommand.class)))
                    .thenThrow(new IllegalStateException("Teacher is not authorized"));

            String body = """
                    {
                        "studentId": "990e8400-e29b-41d4-a716-446655440004",
                        "date": "2026-06-01",
                        "status": "PRESENT"
                    }
                    """;

            mockMvc.perform(post("/api/v1/groups/{groupId}/attendances", GROUP_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    // ======================== GET by date ========================

    @Nested
    @DisplayName("GET /api/v1/groups/{groupId}/attendances/{date}")
    class GetByDate {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() throws Exception {
            AttendanceResult result = buildResult("PRESENT", null);
            when(getByDateUseCase.execute(any(), any())).thenReturn(List.of(result));

            mockMvc.perform(get("/api/v1/groups/{groupId}/attendances/{date}", GROUP_ID, "2026-06-01"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(ATTENDANCE_ID))
                    .andExpect(jsonPath("$[0].status").value("PRESENT"));
        }

        @Test
        @DisplayName("should return 200 with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(getByDateUseCase.execute(any(), any())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/groups/{groupId}/attendances/{date}", GROUP_ID, "2026-06-01"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    // ======================== GET list ========================

    @Nested
    @DisplayName("GET /api/v1/groups/{groupId}/attendances")
    class ListAttendances {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() throws Exception {
            AttendanceResult result = buildResult("PRESENT", null);
            when(listUseCase.execute(any())).thenReturn(List.of(result));

            mockMvc.perform(get("/api/v1/groups/{groupId}/attendances", GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(ATTENDANCE_ID));
        }

        @Test
        @DisplayName("should return 200 with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(listUseCase.execute(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/groups/{groupId}/attendances", GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    // ======================== PUT update ========================

    @Nested
    @DisplayName("PUT /api/v1/groups/{groupId}/attendances/{date}/{studentId}")
    class UpdateAttendance {

        @Test
        @DisplayName("should return 200 with updated attendance")
        void shouldReturn200() throws Exception {
            AttendanceResult result = buildResult("ABSENT", "Left early");
            when(updateUseCase.execute(any(UpdateAttendanceCommand.class))).thenReturn(result);

            String body = """
                    {
                        "status": "ABSENT",
                        "observations": "Left early"
                    }
                    """;

            mockMvc.perform(put("/api/v1/groups/{groupId}/attendances/{date}/{studentId}",
                            GROUP_ID, "2026-06-01", STUDENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ABSENT"))
                    .andExpect(jsonPath("$.observations").value("Left early"));
        }

        @Test
        @DisplayName("should return 422 when not authorized")
        void shouldReturn422WhenNotAuthorized() throws Exception {
            when(updateUseCase.execute(any(UpdateAttendanceCommand.class)))
                    .thenThrow(new IllegalStateException("Teacher is not authorized"));

            String body = """
                    {
                        "status": "ABSENT"
                    }
                    """;

            mockMvc.perform(put("/api/v1/groups/{groupId}/attendances/{date}/{studentId}",
                            GROUP_ID, "2026-06-01", STUDENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 404 when attendance not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(updateUseCase.execute(any(UpdateAttendanceCommand.class)))
                    .thenThrow(new IllegalArgumentException("Attendance not found"));

            String body = """
                    {
                        "status": "ABSENT"
                    }
                    """;

            mockMvc.perform(put("/api/v1/groups/{groupId}/attendances/{date}/{studentId}",
                            GROUP_ID, "2026-06-01", STUDENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }
}
