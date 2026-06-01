package com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.CreateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.UpdateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.application.academic.assessment.port.in.*;
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
@DisplayName("AssessmentController")
class AssessmentControllerTest {

    private MockMvc mockMvc;

    @Mock private CreateAssessmentUseCase createUseCase;
    @Mock private GetAssessmentUseCase getUseCase;
    @Mock private ListAssessmentsByGroupUseCase listUseCase;
    @Mock private UpdateAssessmentUseCase updateUseCase;
    @Mock private DeleteAssessmentUseCase deleteUseCase;

    @InjectMocks
    private AssessmentController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Instant NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String GROUP_ID = "aaa00001-e29b-41d4-a716-446655440001";
    private static final String ASSESSMENT_ID = "asmt-001";
    private static final String TEACHER_ID = "990e8400-e29b-41d4-a716-446655440004";

    private final Principal mockPrincipal = () -> TEACHER_ID;

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private AssessmentResult buildResult(String name, String type, String weight, String maxScore) {
        return new AssessmentResult(
                ASSESSMENT_ID, GROUP_ID, null,
                name, type,
                new BigDecimal(weight), new BigDecimal(maxScore),
                NOW, NOW
        );
    }

    // ======================== POST create ========================

    @Nested
    @DisplayName("POST /api/v1/groups/{groupId}/assessments")
    class CreateAssessment {

        @Test
        @DisplayName("should return 201 with AssessmentResponse")
        void shouldReturn201() throws Exception {
            AssessmentResult result = buildResult("Math Quiz 1", "QUIZ", "15.00", "100.00");
            when(createUseCase.execute(any(CreateAssessmentCommand.class))).thenReturn(result);

            String body = """
                    {
                        "name": "Math Quiz 1",
                        "type": "QUIZ",
                        "weight": 15.00,
                        "maxScore": 100.00
                    }
                    """;

            mockMvc.perform(post("/api/v1/groups/{groupId}/assessments", GROUP_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(ASSESSMENT_ID))
                    .andExpect(jsonPath("$.name").value("Math Quiz 1"))
                    .andExpect(jsonPath("$.type").value("QUIZ"))
                    .andExpect(jsonPath("$.weight").value(15.00))
                    .andExpect(jsonPath("$.maxScore").value(100.00));
        }

        @Test
        @DisplayName("should return 422 when weight <= 0")
        void shouldReturn422ForInvalidWeight() throws Exception {
            when(createUseCase.execute(any(CreateAssessmentCommand.class)))
                    .thenThrow(new IllegalArgumentException("weight must be greater than zero"));

            String body = """
                    {
                        "name": "Bad Quiz",
                        "type": "QUIZ",
                        "weight": 0,
                        "maxScore": 100.00
                    }
                    """;

            mockMvc.perform(post("/api/v1/groups/{groupId}/assessments", GROUP_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 422 when maxScore <= 0")
        void shouldReturn422ForInvalidMaxScore() throws Exception {
            when(createUseCase.execute(any(CreateAssessmentCommand.class)))
                    .thenThrow(new IllegalArgumentException("maxScore must be greater than zero"));

            String body = """
                    {
                        "name": "Bad Quiz",
                        "type": "QUIZ",
                        "weight": 10.00,
                        "maxScore": 0
                    }
                    """;

            mockMvc.perform(post("/api/v1/groups/{groupId}/assessments", GROUP_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 409 when name already exists")
        void shouldReturn409WhenDuplicateName() throws Exception {
            when(createUseCase.execute(any(CreateAssessmentCommand.class)))
                    .thenThrow(new IllegalStateException("already exists"));

            String body = """
                    {
                        "name": "Math Quiz 1",
                        "type": "QUIZ",
                        "weight": 15.00,
                        "maxScore": 100.00
                    }
                    """;

            mockMvc.perform(post("/api/v1/groups/{groupId}/assessments", GROUP_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("should return 422 when teacher is not authorized")
        void shouldReturn422WhenNotAuthorized() throws Exception {
            when(createUseCase.execute(any(CreateAssessmentCommand.class)))
                    .thenThrow(new IllegalStateException("not authorized"));

            String body = """
                    {
                        "name": "Math Quiz 1",
                        "type": "QUIZ",
                        "weight": 15.00,
                        "maxScore": 100.00
                    }
                    """;

            mockMvc.perform(post("/api/v1/groups/{groupId}/assessments", GROUP_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    // ======================== GET by id ========================

    @Nested
    @DisplayName("GET /api/v1/groups/{groupId}/assessments/{id}")
    class GetAssessment {

        @Test
        @DisplayName("should return 200 with AssessmentResponse")
        void shouldReturn200() throws Exception {
            AssessmentResult result = buildResult("Math Quiz 1", "QUIZ", "15.00", "100.00");
            when(getUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(get("/api/v1/groups/{groupId}/assessments/{id}", GROUP_ID, ASSESSMENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ASSESSMENT_ID))
                    .andExpect(jsonPath("$.name").value("Math Quiz 1"));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(getUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("Assessment not found"));

            mockMvc.perform(get("/api/v1/groups/{groupId}/assessments/{id}", GROUP_ID, "nonexistent"))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== GET list ========================

    @Nested
    @DisplayName("GET /api/v1/groups/{groupId}/assessments")
    class ListAssessments {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() throws Exception {
            AssessmentResult result = buildResult("Math Quiz 1", "QUIZ", "15.00", "100.00");
            when(listUseCase.execute(any())).thenReturn(List.of(result));

            mockMvc.perform(get("/api/v1/groups/{groupId}/assessments", GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(ASSESSMENT_ID));
        }

        @Test
        @DisplayName("should return 200 with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(listUseCase.execute(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/groups/{groupId}/assessments", GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    // ======================== PUT update ========================

    @Nested
    @DisplayName("PUT /api/v1/groups/{groupId}/assessments/{id}")
    class UpdateAssessment {

        @Test
        @DisplayName("should return 200 with updated assessment")
        void shouldReturn200() throws Exception {
            AssessmentResult result = buildResult("Updated Quiz", "EXAM", "20.00", "50.00");
            when(updateUseCase.execute(any(UpdateAssessmentCommand.class))).thenReturn(result);

            String body = """
                    {
                        "name": "Updated Quiz",
                        "type": "EXAM",
                        "weight": 20.00,
                        "maxScore": 50.00
                    }
                    """;

            mockMvc.perform(put("/api/v1/groups/{groupId}/assessments/{id}", GROUP_ID, ASSESSMENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Quiz"))
                    .andExpect(jsonPath("$.type").value("EXAM"));
        }

        @Test
        @DisplayName("should return 422 when not authorized")
        void shouldReturn422WhenNotAuthorized() throws Exception {
            when(updateUseCase.execute(any(UpdateAssessmentCommand.class)))
                    .thenThrow(new IllegalStateException("not authorized"));

            String body = """
                    {
                        "name": "Updated",
                        "type": "QUIZ",
                        "weight": 15.00,
                        "maxScore": 100.00
                    }
                    """;

            mockMvc.perform(put("/api/v1/groups/{groupId}/assessments/{id}", GROUP_ID, ASSESSMENT_ID)
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 404 when assessment not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(updateUseCase.execute(any(UpdateAssessmentCommand.class)))
                    .thenThrow(new IllegalArgumentException("Assessment not found"));

            String body = """
                    {
                        "name": "Updated",
                        "type": "QUIZ",
                        "weight": 15.00,
                        "maxScore": 100.00
                    }
                    """;

            mockMvc.perform(put("/api/v1/groups/{groupId}/assessments/{id}", GROUP_ID, "nonexistent")
                            .principal(mockPrincipal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    // ======================== DELETE ========================

    @Nested
    @DisplayName("DELETE /api/v1/groups/{groupId}/assessments/{id}")
    class DeleteAssessment {

        @Test
        @DisplayName("should return 204 when deleted")
        void shouldReturn204() throws Exception {
            doNothing().when(deleteUseCase).execute(any(), any(), any());

            mockMvc.perform(delete("/api/v1/groups/{groupId}/assessments/{id}", GROUP_ID, ASSESSMENT_ID)
                            .principal(mockPrincipal))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 422 when not authorized")
        void shouldReturn422WhenNotAuthorized() throws Exception {
            doThrow(new IllegalStateException("not authorized"))
                    .when(deleteUseCase).execute(any(), any(), any());

            mockMvc.perform(delete("/api/v1/groups/{groupId}/assessments/{id}", GROUP_ID, ASSESSMENT_ID)
                            .principal(mockPrincipal))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 404 when assessment not found")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new IllegalArgumentException("Assessment not found"))
                    .when(deleteUseCase).execute(any(), any(), any());

            mockMvc.perform(delete("/api/v1/groups/{groupId}/assessments/{id}", GROUP_ID, "nonexistent")
                            .principal(mockPrincipal))
                    .andExpect(status().isNotFound());
        }
    }
}
