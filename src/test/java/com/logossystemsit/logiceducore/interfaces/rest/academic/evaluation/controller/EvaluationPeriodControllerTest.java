package com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.CreateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.UpdateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.request.CreateEvaluationPeriodRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.request.UpdateEvaluationPeriodRequest;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluationPeriodController")
class EvaluationPeriodControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateEvaluationPeriodUseCase createUseCase;

    @Mock
    private GetEvaluationPeriodUseCase getUseCase;

    @Mock
    private ListEvaluationPeriodsByPeriodUseCase listUseCase;

    @Mock
    private UpdateEvaluationPeriodUseCase updateUseCase;

    @Mock
    private DeactivateEvaluationPeriodUseCase deactivateUseCase;

    @InjectMocks
    private EvaluationPeriodController controller;

    private final ObjectMapper objectMapper;

    EvaluationPeriodControllerTest() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private static final String PERIOD_ID = "990e8400-e29b-41d4-a716-446655440004";
    private static final String EVAL_ID = "880e8400-e29b-41d4-a716-446655440003";
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("POST /api/v1/periods/{periodId}/evaluations")
    class Create {

        @Test
        @DisplayName("should return 201 when evaluation period created")
        void shouldReturn201() throws Exception {
            CreateEvaluationPeriodRequest request = new CreateEvaluationPeriodRequest(
                    "Examen Parcial", 1, "25.00", "2026-03-01", "2026-03-15"
            );

            EvaluationPeriodResult result = new EvaluationPeriodResult(
                    EVAL_ID, PERIOD_ID, "Examen Parcial", 1,
                    new BigDecimal("25.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15),
                    "ACTIVE", NOW, NOW
            );
            when(createUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(post("/api/v1/periods/{periodId}/evaluations", PERIOD_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Examen Parcial"))
                    .andExpect(jsonPath("$.weight").value(25.00))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should return 409 on weight conflict")
        void shouldReturn409OnConflict() throws Exception {
            CreateEvaluationPeriodRequest request = new CreateEvaluationPeriodRequest(
                    "Examen", 1, "50.00", "2026-03-01", "2026-03-15"
            );
            when(createUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("weight sum would exceed"));

            mockMvc.perform(post("/api/v1/periods/{periodId}/evaluations", PERIOD_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/periods/{periodId}/evaluations/{id}")
    class Get {

        @Test
        @DisplayName("should return 200 with evaluation period")
        void shouldReturn200() throws Exception {
            EvaluationPeriodResult result = new EvaluationPeriodResult(
                    EVAL_ID, PERIOD_ID, "Examen", 1, new BigDecimal("50.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15),
                    "ACTIVE", NOW, NOW
            );
            when(getUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(get("/api/v1/periods/{periodId}/evaluations/{id}", PERIOD_ID, EVAL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Examen"))
                    .andExpect(jsonPath("$.id").value(EVAL_ID));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(getUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("EvaluationPeriod not found"));

            mockMvc.perform(get("/api/v1/periods/{periodId}/evaluations/{id}", PERIOD_ID, EVAL_ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/periods/{periodId}/evaluations")
    class List {

        @Test
        @DisplayName("should return 200 with list")
        void shouldReturn200WithList() throws Exception {
            EvaluationPeriodResult r1 = new EvaluationPeriodResult(
                    "eval-1", PERIOD_ID, "Eval 1", 1, new BigDecimal("50.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15),
                    "ACTIVE", NOW, NOW
            );
            EvaluationPeriodResult r2 = new EvaluationPeriodResult(
                    "eval-2", PERIOD_ID, "Eval 2", 2, new BigDecimal("50.00"),
                    LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 15),
                    "ACTIVE", NOW, NOW
            );
            when(listUseCase.execute(any())).thenReturn(java.util.List.of(r1, r2));

            mockMvc.perform(get("/api/v1/periods/{periodId}/evaluations", PERIOD_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/periods/{periodId}/evaluations/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 on successful update")
        void shouldReturn200() throws Exception {
            UpdateEvaluationPeriodRequest request = new UpdateEvaluationPeriodRequest(
                    "Updated", null, null, null, null
            );
            EvaluationPeriodResult result = new EvaluationPeriodResult(
                    EVAL_ID, PERIOD_ID, "Updated", 1, new BigDecimal("25.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15),
                    "ACTIVE", NOW, NOW
            );
            when(updateUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(put("/api/v1/periods/{periodId}/evaluations/{id}", PERIOD_ID, EVAL_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated"));
        }

        @Test
        @DisplayName("should return 422 when inactive")
        void shouldReturn422WhenInactive() throws Exception {
            UpdateEvaluationPeriodRequest request = new UpdateEvaluationPeriodRequest(
                    "X", null, null, null, null
            );
            when(updateUseCase.execute(any()))
                    .thenThrow(new IllegalStateException("Cannot modify an inactive"));

            mockMvc.perform(put("/api/v1/periods/{periodId}/evaluations/{id}", PERIOD_ID, EVAL_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/periods/{periodId}/evaluations/{id}/deactivate")
    class Deactivate {

        @Test
        @DisplayName("should return 200 on successful deactivation")
        void shouldReturn200() throws Exception {
            EvaluationPeriodResult result = new EvaluationPeriodResult(
                    EVAL_ID, PERIOD_ID, "Eval", 1, new BigDecimal("25.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15),
                    "INACTIVE", NOW, NOW
            );
            when(deactivateUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(patch("/api/v1/periods/{periodId}/evaluations/{id}/deactivate",
                            PERIOD_ID, EVAL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("INACTIVE"));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(deactivateUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("EvaluationPeriod not found"));

            mockMvc.perform(patch("/api/v1/periods/{periodId}/evaluations/{id}/deactivate",
                            PERIOD_ID, EVAL_ID))
                    .andExpect(status().isNotFound());
        }
    }
}
