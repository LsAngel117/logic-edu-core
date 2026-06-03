package com.logossystemsit.logiceducore.interfaces.rest.academic.period.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logossystemsit.logiceducore.application.academic.period.dto.command.CreateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.*;
import com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.request.CreateAcademicPeriodRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.request.UpdateAcademicPeriodRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcademicPeriodController")
class AcademicPeriodControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateAcademicPeriodUseCase createUseCase;

    @Mock
    private GetAcademicPeriodUseCase getUseCase;

    @Mock
    private ListAcademicPeriodsByLevelUseCase listUseCase;

    @Mock
    private UpdateAcademicPeriodUseCase updateUseCase;

    @Mock
    private DeactivateAcademicPeriodUseCase deactivateUseCase;

    @InjectMocks
    private AcademicPeriodController controller;

    private final ObjectMapper objectMapper;

    private static final String LEVEL_ID = "770e8400-e29b-41d4-a716-446655440002";
    private static final String PERIOD_ID = "990e8400-e29b-41d4-a716-446655440004";
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final LocalDate START = LocalDate.of(2026, 3, 1);
    private static final LocalDate END = LocalDate.of(2026, 7, 31);

    public AcademicPeriodControllerTest() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("POST create period")
    class PostCreatePeriod {

        @Test
        @DisplayName("should create period and return 201")
        void shouldCreatePeriodAndReturn201() throws Exception {
            AcademicPeriodResult result = buildResult(PERIOD_ID, "Primer Semestre", "SEMESTER",
                    1, START, END, "ACTIVE");
            when(createUseCase.execute(any())).thenReturn(result);

            CreateAcademicPeriodRequest request = new CreateAcademicPeriodRequest(
                    "SEMESTER", "Primer Semestre", 1,
                    START.toString(), END.toString()
            );

            mockMvc.perform(post("/api/v1/levels/{levelId}/periods", LEVEL_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(PERIOD_ID))
                    .andExpect(jsonPath("$.name").value("Primer Semestre"))
                    .andExpect(jsonPath("$.periodType").value("SEMESTER"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));

            ArgumentCaptor<CreateAcademicPeriodCommand> captor =
                    ArgumentCaptor.forClass(CreateAcademicPeriodCommand.class);
            verify(createUseCase).execute(captor.capture());
            assertThat(captor.getValue().levelId().value()).isEqualTo(LEVEL_ID);
        }

        @Test
        @DisplayName("should return 409 on overlap")
        void shouldReturn409OnOverlap() throws Exception {
            when(createUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("overlap"));

            CreateAcademicPeriodRequest request = new CreateAcademicPeriodRequest(
                    "SEMESTER", "Overlap", 2,
                    START.toString(), END.toString()
            );

            mockMvc.perform(post("/api/v1/levels/{levelId}/periods", LEVEL_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET by id")
    class GetById {

        @Test
        @DisplayName("should return 200 when found")
        void shouldReturn200WhenFound() throws Exception {
            AcademicPeriodResult result = buildResult(PERIOD_ID, "Primer Semestre", "SEMESTER",
                    1, START, END, "ACTIVE");
            when(getUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(get("/api/v1/levels/{levelId}/periods/{id}", LEVEL_ID, PERIOD_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(PERIOD_ID))
                    .andExpect(jsonPath("$.name").value("Primer Semestre"));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(getUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("not found"));

            mockMvc.perform(get("/api/v1/levels/{levelId}/periods/{id}", LEVEL_ID, PERIOD_ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET list by level")
    class GetListByLevel {

        @Test
        @DisplayName("should return 200 with list of periods")
        void shouldReturn200WithList() throws Exception {
            AcademicPeriodResult r1 = buildResult("per-1", "Primer Semestre", "SEMESTER",
                    1, START, END, "ACTIVE");
            AcademicPeriodResult r2 = buildResult("per-2", "Segundo Semestre", "SEMESTER",
                    2, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 11, 30), "ACTIVE");
            when(listUseCase.execute(any())).thenReturn(List.of(r1, r2));

            mockMvc.perform(get("/api/v1/levels/{levelId}/periods", LEVEL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Primer Semestre"))
                    .andExpect(jsonPath("$[1].name").value("Segundo Semestre"));
        }

        @Test
        @DisplayName("should return 200 with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(listUseCase.execute(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/levels/{levelId}/periods", LEVEL_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("PUT update period")
    class PutUpdatePeriod {

        @Test
        @DisplayName("should update and return 200")
        void shouldUpdateAndReturn200() throws Exception {
            AcademicPeriodResult result = buildResult(PERIOD_ID, "Updated Semestre", "SEMESTER",
                    1, START, END, "ACTIVE");
            when(updateUseCase.execute(any())).thenReturn(result);

            UpdateAcademicPeriodRequest request = new UpdateAcademicPeriodRequest(
                    "Updated Semestre", null, null, null, null
            );

            mockMvc.perform(put("/api/v1/levels/{levelId}/periods/{id}", LEVEL_ID, PERIOD_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Semestre"));
        }

        @Test
        @DisplayName("should return 409 on overlap during update")
        void shouldReturn409OnOverlapDuringUpdate() throws Exception {
            when(updateUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("overlap"));

            UpdateAcademicPeriodRequest request = new UpdateAcademicPeriodRequest(
                    null, null, null, START.toString(), END.toString()
            );

            mockMvc.perform(put("/api/v1/levels/{levelId}/periods/{id}", LEVEL_ID, PERIOD_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("should return 404 when not found on update")
        void shouldReturn404WhenNotFoundOnUpdate() throws Exception {
            when(updateUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("not found"));

            UpdateAcademicPeriodRequest request = new UpdateAcademicPeriodRequest(
                    "X", null, null, null, null
            );

            mockMvc.perform(put("/api/v1/levels/{levelId}/periods/{id}", LEVEL_ID, PERIOD_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 422 when period is inactive")
        void shouldReturn422WhenInactiveOnUpdate() throws Exception {
            when(updateUseCase.execute(any()))
                    .thenThrow(new IllegalStateException("inactive"));

            UpdateAcademicPeriodRequest request = new UpdateAcademicPeriodRequest(
                    "X", null, null, null, null
            );

            mockMvc.perform(put("/api/v1/levels/{levelId}/periods/{id}", LEVEL_ID, PERIOD_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PATCH deactivate")
    class PatchDeactivate {

        @Test
        @DisplayName("should deactivate and return 200")
        void shouldDeactivateAndReturn200() throws Exception {
            AcademicPeriodResult result = buildResult(PERIOD_ID, "Primer Semestre", "SEMESTER",
                    1, START, END, "INACTIVE");
            when(deactivateUseCase.execute(any())).thenReturn(result);

            mockMvc.perform(patch("/api/v1/levels/{levelId}/periods/{id}/deactivate",
                            LEVEL_ID, PERIOD_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("INACTIVE"));
        }

        @Test
        @DisplayName("should return 422 when already inactive")
        void shouldReturn422WhenAlreadyInactive() throws Exception {
            when(deactivateUseCase.execute(any()))
                    .thenThrow(new IllegalStateException("already inactive"));

            mockMvc.perform(patch("/api/v1/levels/{levelId}/periods/{id}/deactivate",
                            LEVEL_ID, PERIOD_ID))
                    .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(deactivateUseCase.execute(any()))
                    .thenThrow(new IllegalArgumentException("not found"));

            mockMvc.perform(patch("/api/v1/levels/{levelId}/periods/{id}/deactivate",
                            LEVEL_ID, PERIOD_ID))
                    .andExpect(status().isNotFound());
        }
    }

    private AcademicPeriodResult buildResult(String id, String name, String periodType,
                                              int sequence, LocalDate startDate,
                                              LocalDate endDate, String status) {
        return new AcademicPeriodResult(id, LEVEL_ID, periodType, name, sequence,
                startDate, endDate, status, NOW, NOW);
    }
}
