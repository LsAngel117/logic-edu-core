package com.logossystemsit.logiceducore.interfaces.rest.academic.level.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logossystemsit.logiceducore.application.academic.level.dto.command.CreateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.command.UpdateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.request.CreateAcademicLevelRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.request.UpdateAcademicLevelRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcademicLevelController")
class AcademicLevelControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateAcademicLevelUseCase createUseCase;

    @Mock
    private GetAcademicLevelUseCase getUseCase;

    @Mock
    private ListAcademicLevelsBySchoolUseCase listUseCase;

    @Mock
    private UpdateAcademicLevelUseCase updateUseCase;

    @Mock
    private DeactivateAcademicLevelUseCase deactivateUseCase;

    @InjectMocks
    private AcademicLevelController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SCHOOL_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String LEVEL_ID = "770e8400-e29b-41d4-a716-446655440002";
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST should create level and return 201")
    void postShouldCreateLevelAndReturn201() throws Exception {
        AcademicLevelResult result = buildResult(LEVEL_ID, "Primaria", 1, "ACTIVE");
        when(createUseCase.execute(any())).thenReturn(result);

        CreateAcademicLevelRequest request = new CreateAcademicLevelRequest("Primaria", 1);

        mockMvc.perform(post("/api/v1/schools/{schoolId}/levels", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(LEVEL_ID))
                .andExpect(jsonPath("$.name").value("Primaria"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        ArgumentCaptor<CreateAcademicLevelCommand> captor =
                ArgumentCaptor.forClass(CreateAcademicLevelCommand.class);
        verify(createUseCase).execute(captor.capture());
        assertThat(captor.getValue().schoolId().value()).isEqualTo(SCHOOL_ID);
    }

    @Test
    @DisplayName("POST should return 409 on duplicate number")
    void postShouldReturn409OnDuplicate() throws Exception {
        when(createUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("already exists"));

        CreateAcademicLevelRequest request = new CreateAcademicLevelRequest("Primaria", 1);

        mockMvc.perform(post("/api/v1/schools/{schoolId}/levels", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET by id should return 200")
    void getByIdShouldReturn200() throws Exception {
        AcademicLevelResult result = buildResult(LEVEL_ID, "Secundaria", 2, "ACTIVE");
        when(getUseCase.execute(any())).thenReturn(result);

        mockMvc.perform(get("/api/v1/schools/{schoolId}/levels/{id}", SCHOOL_ID, LEVEL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(LEVEL_ID))
                .andExpect(jsonPath("$.name").value("Secundaria"))
                .andExpect(jsonPath("$.number").value(2));
    }

    @Test
    @DisplayName("GET by id should return 404 when not found")
    void getByIdShouldReturn404WhenNotFound() throws Exception {
        when(getUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("not found"));

        mockMvc.perform(get("/api/v1/schools/{schoolId}/levels/{id}", SCHOOL_ID, LEVEL_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET list by schoolId should return 200 with list")
    void listBySchoolIdShouldReturn200WithList() throws Exception {
        AcademicLevelResult r1 = buildResult("id-1", "Primaria", 1, "ACTIVE");
        AcademicLevelResult r2 = buildResult("id-2", "Secundaria", 2, "ACTIVE");
        when(listUseCase.execute(any())).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/schools/{schoolId}/levels", SCHOOL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Primaria"))
                .andExpect(jsonPath("$[1].name").value("Secundaria"));
    }

    @Test
    @DisplayName("GET list by schoolId should return 200 with empty list")
    void listBySchoolIdShouldReturnEmptyList() throws Exception {
        when(listUseCase.execute(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/schools/{schoolId}/levels", SCHOOL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("PUT should update and return 200")
    void putShouldUpdateAndReturn200() throws Exception {
        AcademicLevelResult result = buildResult(LEVEL_ID, "Primaria Avanzada", 1, "ACTIVE");
        when(updateUseCase.execute(any())).thenReturn(result);

        UpdateAcademicLevelRequest request = new UpdateAcademicLevelRequest("Primaria Avanzada", 1);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/levels/{id}", SCHOOL_ID, LEVEL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Primaria Avanzada"));
    }

    @Test
    @DisplayName("PUT should return 409 on duplicate number")
    void putShouldReturn409OnDuplicate() throws Exception {
        when(updateUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("already exists"));

        UpdateAcademicLevelRequest request = new UpdateAcademicLevelRequest("X", 2);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/levels/{id}", SCHOOL_ID, LEVEL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT should return 404 when not found")
    void putShouldReturn404WhenNotFound() throws Exception {
        when(updateUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("AcademicLevel not found"));

        UpdateAcademicLevelRequest request = new UpdateAcademicLevelRequest("X", 1);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/levels/{id}", SCHOOL_ID, LEVEL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT should return 422 when level is inactive")
    void putShouldReturn422WhenInactive() throws Exception {
        when(updateUseCase.execute(any()))
                .thenThrow(new IllegalStateException("inactive"));

        UpdateAcademicLevelRequest request = new UpdateAcademicLevelRequest("X", 1);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/levels/{id}", SCHOOL_ID, LEVEL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH deactivate should return 200")
    void patchDeactivateShouldReturn200() throws Exception {
        AcademicLevelResult result = buildResult(LEVEL_ID, "Primaria", 1, "INACTIVE");
        when(deactivateUseCase.execute(any())).thenReturn(result);

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/levels/{id}/deactivate", SCHOOL_ID, LEVEL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("PATCH deactivate should return 422 when already inactive")
    void patchDeactivateShouldReturn422WhenAlreadyInactive() throws Exception {
        when(deactivateUseCase.execute(any()))
                .thenThrow(new IllegalStateException("already inactive"));

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/levels/{id}/deactivate", SCHOOL_ID, LEVEL_ID))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH deactivate should return 404 when not found")
    void patchDeactivateShouldReturn404WhenNotFound() throws Exception {
        when(deactivateUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("not found"));

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/levels/{id}/deactivate", SCHOOL_ID, LEVEL_ID))
                .andExpect(status().isNotFound());
    }

    private AcademicLevelResult buildResult(String id, String name, int number, String status) {
        return new AcademicLevelResult(id, SCHOOL_ID, name, number, status, NOW, NOW);
    }
}
