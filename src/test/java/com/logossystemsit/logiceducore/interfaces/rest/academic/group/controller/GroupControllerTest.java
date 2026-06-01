package com.logossystemsit.logiceducore.interfaces.rest.academic.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logossystemsit.logiceducore.application.academic.group.dto.command.CreateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.command.ScheduleData;
import com.logossystemsit.logiceducore.application.academic.group.dto.command.UpdateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.*;
import com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request.CreateGroupRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request.UpdateGroupRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request.UpdateSchedulesRequest;
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
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GroupController")
class GroupControllerTest {

    private MockMvc mockMvc;

    @Mock private CreateGroupUseCase createUseCase;
    @Mock private GetGroupUseCase getUseCase;
    @Mock private ListGroupsBySchoolUseCase listUseCase;
    @Mock private UpdateGroupUseCase updateUseCase;
    @Mock private UpdateGroupSchedulesUseCase updateSchedulesUseCase;
    @Mock private DeactivateGroupUseCase deactivateUseCase;

    @InjectMocks
    private GroupController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Instant NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String SCHOOL_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String GROUP_ID = "aaa00001-e29b-41d4-a716-446655440001";

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private GroupResult buildResult(String code, int capacity, String status) {
        return new GroupResult(
                GROUP_ID, SCHOOL_ID, "sub-001", "per-001", "branch-001",
                "teacher-001", code, capacity, status, 0L,
                List.of(new GroupResult.ScheduleResult("sched-1", "MONDAY",
                        LocalTime.of(8, 0), LocalTime.of(10, 0), "Room 101")),
                NOW, NOW
        );
    }

    // ======================== POST create ========================

    @Nested
    @DisplayName("POST /api/v1/schools/{schoolId}/groups")
    class CreateGroup {

        @Test
        @DisplayName("should return 201 with GroupResponse")
        void shouldReturn201WithGroupResponse() throws Exception {
            GroupResult result = buildResult("MATH-101", 30, "ACTIVE");
            when(createUseCase.execute(any(CreateGroupCommand.class))).thenReturn(result);

            String body = """
                    {
                        "subjectId": "sub-001",
                        "academicPeriodId": "per-001",
                        "branchId": "branch-001",
                        "teacherId": "990e8400-e29b-41d4-a716-446655440004",
                        "code": "MATH-101",
                        "capacity": 30,
                        "schedules": [
                            {"dayOfWeek": "MONDAY", "startTime": "08:00", "endTime": "10:00", "classroom": "Room 101"}
                        ]
                    }
                    """;

            mockMvc.perform(post("/api/v1/schools/{schoolId}/groups", SCHOOL_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(GROUP_ID))
                    .andExpect(jsonPath("$.code").value("MATH-101"))
                    .andExpect(jsonPath("$.capacity").value(30))
                    .andExpect(jsonPath("$.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.schedules[0].dayOfWeek").value("MONDAY"));
        }

        @Test
        @DisplayName("should return 409 on duplicate code")
        void shouldReturn409OnDuplicateCode() throws Exception {
            when(createUseCase.execute(any(CreateGroupCommand.class)))
                    .thenThrow(new IllegalArgumentException("Group code MATH-101 already exists"));

            String body = """
                    {
                        "subjectId": "sub-001",
                        "academicPeriodId": "per-001",
                        "branchId": "branch-001",
                        "teacherId": "990e8400-e29b-41d4-a716-446655440004",
                        "code": "MATH-101",
                        "capacity": 30,
                        "schedules": []
                    }
                    """;

            mockMvc.perform(post("/api/v1/schools/{schoolId}/groups", SCHOOL_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("should return 422 on inactive subject")
        void shouldReturn422OnInactiveSubject() throws Exception {
            when(createUseCase.execute(any(CreateGroupCommand.class)))
                    .thenThrow(new IllegalStateException("Subject is not active"));

            String body = """
                    {
                        "subjectId": "sub-001",
                        "academicPeriodId": "per-001",
                        "branchId": "branch-001",
                        "teacherId": "990e8400-e29b-41d4-a716-446655440004",
                        "code": "MATH-101",
                        "capacity": 30,
                        "schedules": []
                    }
                    """;

            mockMvc.perform(post("/api/v1/schools/{schoolId}/groups", SCHOOL_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    // ======================== GET getById ========================

    @Nested
    @DisplayName("GET /api/v1/schools/{schoolId}/groups/{id}")
    class GetGroup {

        @Test
        @DisplayName("should return 200 with GroupResponse")
        void shouldReturn200WithGroupResponse() throws Exception {
            GroupResult result = buildResult("MATH-101", 30, "ACTIVE");
            when(getUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(get("/api/v1/schools/{schoolId}/groups/{id}", SCHOOL_ID, GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(GROUP_ID))
                    .andExpect(jsonPath("$.code").value("MATH-101"));
        }

        @Test
        @DisplayName("should return 404 when group not found")
        void shouldReturn404WhenGroupNotFound() throws Exception {
            when(getUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("Group not found"));

            mockMvc.perform(get("/api/v1/schools/{schoolId}/groups/{id}", SCHOOL_ID, "nonexistent"))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== GET list ========================

    @Nested
    @DisplayName("GET /api/v1/schools/{schoolId}/groups")
    class ListGroups {

        @Test
        @DisplayName("should return 200 with list of groups")
        void shouldReturn200WithListOfGroups() throws Exception {
            GroupResult r1 = buildResult("MATH-101", 30, "ACTIVE");
            GroupResult r2 = buildResult("PHY-201", 25, "ACTIVE");
            when(listUseCase.execute(any(), isNull(), isNull())).thenReturn(List.of(r1, r2));

            mockMvc.perform(get("/api/v1/schools/{schoolId}/groups", SCHOOL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("should return 200 with empty list when no groups")
        void shouldReturn200WithEmptyList() throws Exception {
            when(listUseCase.execute(any(), isNull(), isNull())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/schools/{schoolId}/groups", SCHOOL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("should filter by branchId")
        void shouldFilterByBranchId() throws Exception {
            when(listUseCase.execute(any(), any(), isNull())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/schools/{schoolId}/groups", SCHOOL_ID)
                            .param("branchId", "branch-001"))
                    .andExpect(status().isOk());

            verify(listUseCase).execute(any(), any(), isNull());
        }

        @Test
        @DisplayName("should filter by periodId")
        void shouldFilterByPeriodId() throws Exception {
            when(listUseCase.execute(any(), isNull(), any())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/schools/{schoolId}/groups", SCHOOL_ID)
                            .param("periodId", "per-001"))
                    .andExpect(status().isOk());

            verify(listUseCase).execute(any(), isNull(), any());
        }
    }

    // ======================== PUT update ========================

    @Nested
    @DisplayName("PUT /api/v1/schools/{schoolId}/groups/{id}")
    class UpdateGroup {

        @Test
        @DisplayName("should return 200 when group updated")
        void shouldReturn200WhenGroupUpdated() throws Exception {
            GroupResult result = buildResult("MATH-202", 25, "ACTIVE");
            when(updateUseCase.execute(any(UpdateGroupCommand.class))).thenReturn(result);

            String body = """
                    {
                        "subjectId": "sub-001",
                        "academicPeriodId": "per-001",
                        "branchId": "branch-001",
                        "teacherId": "990e8400-e29b-41d4-a716-446655440004",
                        "code": "MATH-202",
                        "capacity": 25
                    }
                    """;

            mockMvc.perform(put("/api/v1/schools/{schoolId}/groups/{id}", SCHOOL_ID, GROUP_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("MATH-202"))
                    .andExpect(jsonPath("$.capacity").value(25));
        }

        @Test
        @DisplayName("should return 422 when group is inactive")
        void shouldReturn422WhenGroupIsInactive() throws Exception {
            when(updateUseCase.execute(any(UpdateGroupCommand.class)))
                    .thenThrow(new IllegalStateException("Cannot update an inactive group"));

            String body = """
                    {
                        "subjectId": "sub-001",
                        "academicPeriodId": "per-001",
                        "branchId": "branch-001",
                        "teacherId": "990e8400-e29b-41d4-a716-446655440004",
                        "code": "MATH-202",
                        "capacity": 25
                    }
                    """;

            mockMvc.perform(put("/api/v1/schools/{schoolId}/groups/{id}", SCHOOL_ID, GROUP_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 409 on duplicate code")
        void shouldReturn409OnDuplicateCode() throws Exception {
            when(updateUseCase.execute(any(UpdateGroupCommand.class)))
                    .thenThrow(new IllegalArgumentException("Group code MATH-202 already exists"));

            String body = """
                    {
                        "subjectId": "sub-001",
                        "academicPeriodId": "per-001",
                        "branchId": "branch-001",
                        "teacherId": "990e8400-e29b-41d4-a716-446655440004",
                        "code": "MATH-202",
                        "capacity": 25
                    }
                    """;

            mockMvc.perform(put("/api/v1/schools/{schoolId}/groups/{id}", SCHOOL_ID, GROUP_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }
    }

    // ======================== PUT update schedules ========================

    @Nested
    @DisplayName("PUT /api/v1/schools/{schoolId}/groups/{id}/schedules")
    class UpdateSchedules {

        @Test
        @DisplayName("should return 200 with new schedules")
        void shouldReturn200WithNewSchedules() throws Exception {
            GroupResult result = buildResult("MATH-101", 30, "ACTIVE");
            when(updateSchedulesUseCase.execute(any(), anyList())).thenReturn(result);

            String body = """
                    {
                        "schedules": [
                            {"dayOfWeek": "TUESDAY", "startTime": "14:00", "endTime": "16:00"}
                        ]
                    }
                    """;

            mockMvc.perform(put("/api/v1/schools/{schoolId}/groups/{id}/schedules", SCHOOL_ID, GROUP_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should return 422 when group is inactive")
        void shouldReturn422WhenGroupIsInactive() throws Exception {
            when(updateSchedulesUseCase.execute(any(), anyList()))
                    .thenThrow(new IllegalStateException("Cannot update schedules of an inactive group"));

            String body = """
                    {
                        "schedules": [
                            {"dayOfWeek": "TUESDAY", "startTime": "14:00", "endTime": "16:00"}
                        ]
                    }
                    """;

            mockMvc.perform(put("/api/v1/schools/{schoolId}/groups/{id}/schedules", SCHOOL_ID, GROUP_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 404 when group not found")
        void shouldReturn404WhenGroupNotFound() throws Exception {
            when(updateSchedulesUseCase.execute(any(), anyList()))
                    .thenThrow(new IllegalArgumentException("Group not found"));

            String body = "{\"schedules\": []}";

            mockMvc.perform(put("/api/v1/schools/{schoolId}/groups/{id}/schedules", SCHOOL_ID, GROUP_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== PATCH deactivate ========================

    @Nested
    @DisplayName("PATCH /api/v1/schools/{schoolId}/groups/{id}/deactivate")
    class DeactivateGroup {

        @Test
        @DisplayName("should return 200 with INACTIVE status")
        void shouldReturn200WithInactiveStatus() throws Exception {
            GroupResult result = buildResult("MATH-101", 30, "INACTIVE");
            when(deactivateUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(patch("/api/v1/schools/{schoolId}/groups/{id}/deactivate", SCHOOL_ID, GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("INACTIVE"));
        }

        @Test
        @DisplayName("should return 404 when group not found")
        void shouldReturn404WhenGroupNotFound() throws Exception {
            when(deactivateUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("Group not found"));

            mockMvc.perform(patch("/api/v1/schools/{schoolId}/groups/{id}/deactivate", SCHOOL_ID, "nonexistent"))
                    .andExpect(status().isNotFound());
        }
    }
}
