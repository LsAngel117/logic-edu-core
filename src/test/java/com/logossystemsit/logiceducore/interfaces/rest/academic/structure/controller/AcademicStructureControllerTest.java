package com.logossystemsit.logiceducore.interfaces.rest.academic.structure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logossystemsit.logiceducore.application.academic.structure.dto.command.CreateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.command.UpdateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.application.academic.structure.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.StructureType;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.request.CreateAcademicStructureRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.request.UpdateAcademicStructureRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcademicStructureController")
class AcademicStructureControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateAcademicStructureUseCase createUseCase;

    @Mock
    private GetAcademicStructureUseCase getUseCase;

    @Mock
    private UpdateAcademicStructureUseCase updateUseCase;

    @Mock
    private DeactivateAcademicStructureUseCase deactivateUseCase;

    @InjectMocks
    private AcademicStructureController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SCHOOL_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String STRUCTURE_ID = "660e8400-e29b-41d4-a716-446655440001";
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST should create structure and return 201")
    void postShouldCreateStructureAndReturn201() throws Exception {
        AcademicStructureResult result = buildResult(STRUCTURE_ID, "SEMESTRAL", true, 1);
        when(createUseCase.execute(any())).thenReturn(result);

        CreateAcademicStructureRequest request = new CreateAcademicStructureRequest(
                "SEMESTRAL", 10, 2, 3, 6, 45
        );

        mockMvc.perform(post("/api/v1/schools/{schoolId}/structures", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(STRUCTURE_ID))
                .andExpect(jsonPath("$.structureType").value("SEMESTRAL"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.version").value(1));

        ArgumentCaptor<CreateAcademicStructureCommand> captor =
                ArgumentCaptor.forClass(CreateAcademicStructureCommand.class);
        verify(createUseCase).execute(captor.capture());
        assertThat(captor.getValue().schoolId().value()).isEqualTo(SCHOOL_ID);
    }

    @Test
    @DisplayName("POST should return 409 on duplicate active structure")
    void postShouldReturn409OnDuplicate() throws Exception {
        when(createUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("An active structure already exists"));

        CreateAcademicStructureRequest request = new CreateAcademicStructureRequest(
                "SEMESTRAL", 10, 2, 3, 6, 45
        );

        mockMvc.perform(post("/api/v1/schools/{schoolId}/structures", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET by id should return 200")
    void getByIdShouldReturn200() throws Exception {
        AcademicStructureResult result = buildResult(STRUCTURE_ID, "TRIMESTRAL", true, 1);
        when(getUseCase.execute(any())).thenReturn(result);

        mockMvc.perform(get("/api/v1/schools/{schoolId}/structures/{id}", SCHOOL_ID, STRUCTURE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STRUCTURE_ID))
                .andExpect(jsonPath("$.structureType").value("TRIMESTRAL"));
    }

    @Test
    @DisplayName("GET by id should return 404 when not found")
    void getByIdShouldReturn404WhenNotFound() throws Exception {
        when(getUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("AcademicStructure not found"));

        mockMvc.perform(get("/api/v1/schools/{schoolId}/structures/{id}", SCHOOL_ID, STRUCTURE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET active should return 200")
    void getActiveShouldReturn200() throws Exception {
        AcademicStructureResult result = buildResult(STRUCTURE_ID, "ANUAL", true, 2);
        when(getUseCase.findActiveBySchoolId(any())).thenReturn(result);

        mockMvc.perform(get("/api/v1/schools/{schoolId}/structures/active", SCHOOL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("GET active should return 404 when no active structure")
    void getActiveShouldReturn404WhenNoActive() throws Exception {
        when(getUseCase.findActiveBySchoolId(any()))
                .thenThrow(new IllegalArgumentException("No active AcademicStructure found"));

        mockMvc.perform(get("/api/v1/schools/{schoolId}/structures/active", SCHOOL_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT should update and return 200")
    void putShouldUpdateAndReturn200() throws Exception {
        AcademicStructureResult result = buildResult(STRUCTURE_ID, "MODULAR", true, 2);
        when(updateUseCase.execute(any())).thenReturn(result);

        UpdateAcademicStructureRequest request = new UpdateAcademicStructureRequest(
                "MODULAR", 4, 3, 2, 8, 60
        );

        mockMvc.perform(put("/api/v1/schools/{schoolId}/structures/{id}", SCHOOL_ID, STRUCTURE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(2))
                .andExpect(jsonPath("$.structureType").value("MODULAR"));
    }

    @Test
    @DisplayName("PUT should return 422 when structure is inactive")
    void putShouldReturn422WhenInactive() throws Exception {
        when(updateUseCase.execute(any()))
                .thenThrow(new IllegalStateException("Cannot modify an inactive structure"));

        UpdateAcademicStructureRequest request = new UpdateAcademicStructureRequest(
                "MODULAR", 4, 3, 2, 8, 60
        );

        mockMvc.perform(put("/api/v1/schools/{schoolId}/structures/{id}", SCHOOL_ID, STRUCTURE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH deactivate should return 200")
    void patchDeactivateShouldReturn200() throws Exception {
        AcademicStructureResult result = buildResult(STRUCTURE_ID, "SEMESTRAL", false, 1);
        when(deactivateUseCase.execute(any())).thenReturn(result);

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/structures/{id}/deactivate", SCHOOL_ID, STRUCTURE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("PATCH deactivate should return 409 when already inactive")
    void patchDeactivateShouldReturn409WhenAlreadyInactive() throws Exception {
        when(deactivateUseCase.execute(any()))
                .thenThrow(new IllegalStateException("already inactive"));

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/structures/{id}/deactivate", SCHOOL_ID, STRUCTURE_ID))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PATCH deactivate should return 404 when not found")
    void patchDeactivateShouldReturn404WhenNotFound() throws Exception {
        when(deactivateUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("not found"));

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/structures/{id}/deactivate", SCHOOL_ID, STRUCTURE_ID))
                .andExpect(status().isNotFound());
    }

    private AcademicStructureResult buildResult(String id, String type, boolean active, int version) {
        return new AcademicStructureResult(id, SCHOOL_ID, type, 10, 2, 3, 6, 45, active, version, NOW, NOW);
    }
}
