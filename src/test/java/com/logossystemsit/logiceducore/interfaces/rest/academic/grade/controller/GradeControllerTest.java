package com.logossystemsit.logiceducore.interfaces.rest.academic.grade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logossystemsit.logiceducore.application.academic.grade.dto.command.RegisterGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.command.UpdateGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.application.academic.grade.port.in.*;
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

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GradeController")
class GradeControllerTest {

    private MockMvc mockMvc;

    @Mock private RegisterGradeUseCase registerUseCase;
    @Mock private GetGradeUseCase getUseCase;
    @Mock private ListGradesByAssessmentUseCase listUseCase;
    @Mock private UpdateGradeUseCase updateUseCase;

    @InjectMocks
    private GradeController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Instant NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String ASSESSMENT_ID = "aaa00001-e29b-41d4-a716-446655440001";
    private static final String GRADE_ID = "grd-001";
    private static final String STUDENT_ID = "990e8400-e29b-41d4-a716-446655440004";
    private static final String TEACHER_ID = "990e8400-e29b-41d4-a716-446655440004";

    private final Principal mockPrincipal = () -> TEACHER_ID;

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private GradeResult buildResult(BigDecimal value) {
        return new GradeResult(GRADE_ID, ASSESSMENT_ID, STUDENT_ID, value, NOW, NOW);
    }

    // ======================== POST register ========================

    @Nested
    @DisplayName("POST /api/v1/assessments/{assessmentId}/grades")
    class RegisterGrade {

        @Test
        @DisplayName("should return 201 with GradeResponse")
        void shouldReturn201() throws Exception {
            GradeResult result = buildResult(new BigDecimal("8.50"));
            when(registerUseCase.execute(any(RegisterGradeCommand.class))).thenReturn(result);

            String body = """
                    {
                        "studentId": "990e8400-e29b-41d4-a716-446655440004",
                        "value": 8.50
                    }
                    """;

            mockMvc.perform(post("/api/v1/assessments/{assessmentId}/grades", ASSESSMENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(GRADE_ID))
                    .andExpect(jsonPath("$.value").value(8.50))
                    .andExpect(jsonPath("$.studentId").value(STUDENT_ID));
        }

        @Test
        @DisplayName("should return 422 when service throws IllegalArgumentException")
        void shouldReturn422OnIllegalArgument() throws Exception {
            when(registerUseCase.execute(any(RegisterGradeCommand.class)))
                    .thenThrow(new IllegalArgumentException("Assessment not found"));

            String body = """
                    {
                        "studentId": "990e8400-e29b-41d4-a716-446655440004",
                        "value": 8.50
                    }
                    """;

            mockMvc.perform(post("/api/v1/assessments/{assessmentId}/grades", ASSESSMENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 422 when teacher is not authorized")
        void shouldReturn422WhenNotAuthorized() throws Exception {
            when(registerUseCase.execute(any(RegisterGradeCommand.class)))
                    .thenThrow(new IllegalStateException("Teacher is not authorized"));

            String body = """
                    {
                        "studentId": "990e8400-e29b-41d4-a716-446655440004",
                        "value": 8.50
                    }
                    """;

            mockMvc.perform(post("/api/v1/assessments/{assessmentId}/grades", ASSESSMENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 422 when value exceeds maxScore")
        void shouldReturn422WhenValueExceedsMaxScore() throws Exception {
            when(registerUseCase.execute(any(RegisterGradeCommand.class)))
                    .thenThrow(new IllegalStateException("Grade value exceeds max score"));

            String body = """
                    {
                        "studentId": "990e8400-e29b-41d4-a716-446655440004",
                        "value": 15.00
                    }
                    """;

            mockMvc.perform(post("/api/v1/assessments/{assessmentId}/grades", ASSESSMENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    // ======================== GET by ID ========================

    @Nested
    @DisplayName("GET /api/v1/assessments/{assessmentId}/grades/{id}")
    class GetGrade {

        @Test
        @DisplayName("should return 200 with GradeResponse")
        void shouldReturn200() throws Exception {
            GradeResult result = buildResult(new BigDecimal("8.50"));
            when(getUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(get("/api/v1/assessments/{assessmentId}/grades/{id}",
                            ASSESSMENT_ID, GRADE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(GRADE_ID))
                    .andExpect(jsonPath("$.value").value(8.50));
        }

        @Test
        @DisplayName("should return 404 when grade not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(getUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("Grade not found"));

            mockMvc.perform(get("/api/v1/assessments/{assessmentId}/grades/{id}",
                            ASSESSMENT_ID, GRADE_ID))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== GET list ========================

    @Nested
    @DisplayName("GET /api/v1/assessments/{assessmentId}/grades")
    class ListGrades {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() throws Exception {
            GradeResult result = buildResult(new BigDecimal("8.50"));
            when(listUseCase.execute(any())).thenReturn(List.of(result));

            mockMvc.perform(get("/api/v1/assessments/{assessmentId}/grades", ASSESSMENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(GRADE_ID))
                    .andExpect(jsonPath("$[0].value").value(8.50));
        }

        @Test
        @DisplayName("should return 200 with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(listUseCase.execute(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/assessments/{assessmentId}/grades", ASSESSMENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    // ======================== PUT update ========================

    @Nested
    @DisplayName("PUT /api/v1/assessments/{assessmentId}/grades/{studentId}")
    class UpdateGrade {

        @Test
        @DisplayName("should return 200 with updated grade")
        void shouldReturn200() throws Exception {
            GradeResult result = buildResult(new BigDecimal("9.50"));
            when(updateUseCase.execute(any(UpdateGradeCommand.class))).thenReturn(result);

            String body = """
                    {
                        "value": 9.50
                    }
                    """;

            mockMvc.perform(put("/api/v1/assessments/{assessmentId}/grades/{studentId}",
                            ASSESSMENT_ID, STUDENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.value").value(9.50));
        }

        @Test
        @DisplayName("should return 422 when not authorized")
        void shouldReturn422WhenNotAuthorized() throws Exception {
            when(updateUseCase.execute(any(UpdateGradeCommand.class)))
                    .thenThrow(new IllegalStateException("Teacher is not authorized"));

            String body = """
                    {
                        "value": 9.50
                    }
                    """;

            mockMvc.perform(put("/api/v1/assessments/{assessmentId}/grades/{studentId}",
                            ASSESSMENT_ID, STUDENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 404 when grade not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(updateUseCase.execute(any(UpdateGradeCommand.class)))
                    .thenThrow(new IllegalArgumentException("Grade not found"));

            String body = """
                    {
                        "value": 9.50
                    }
                    """;

            mockMvc.perform(put("/api/v1/assessments/{assessmentId}/grades/{studentId}",
                            ASSESSMENT_ID, STUDENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 422 when value exceeds maxScore")
        void shouldReturn422WhenValueExceedsMaxScore() throws Exception {
            when(updateUseCase.execute(any(UpdateGradeCommand.class)))
                    .thenThrow(new IllegalStateException("Grade value exceeds max score"));

            String body = """
                    {
                        "value": 15.00
                    }
                    """;

            mockMvc.perform(put("/api/v1/assessments/{assessmentId}/grades/{studentId}",
                            ASSESSMENT_ID, STUDENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }
    }
}
