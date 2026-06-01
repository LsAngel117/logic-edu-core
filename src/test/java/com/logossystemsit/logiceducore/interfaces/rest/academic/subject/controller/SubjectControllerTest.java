package com.logossystemsit.logiceducore.interfaces.rest.academic.subject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logossystemsit.logiceducore.application.academic.subject.dto.command.CreateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.command.UpdateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.request.CreateSubjectRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.request.UpdateSubjectRequest;
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
@DisplayName("SubjectController")
class SubjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateSubjectUseCase createUseCase;

    @Mock
    private GetSubjectUseCase getUseCase;

    @Mock
    private ListSubjectsBySchoolUseCase listUseCase;

    @Mock
    private UpdateSubjectUseCase updateUseCase;

    @Mock
    private DeactivateSubjectUseCase deactivateUseCase;

    @InjectMocks
    private SubjectController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SCHOOL_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String SUBJECT_ID = "660e8400-e29b-41d4-a716-446655440001";
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST should create subject and return 201")
    void postShouldCreateSubjectAndReturn201() throws Exception {
        SubjectResult result = buildResult(SUBJECT_ID, "MAT-101", "Matematicas", "Curso de matematicas", 80, "ACTIVE");
        when(createUseCase.execute(any())).thenReturn(result);

        CreateSubjectRequest request = new CreateSubjectRequest("MAT-101", "Matematicas", "Curso de matematicas", 80);

        mockMvc.perform(post("/api/v1/schools/{schoolId}/subjects", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SUBJECT_ID))
                .andExpect(jsonPath("$.schoolId").value(SCHOOL_ID))
                .andExpect(jsonPath("$.code").value("MAT-101"))
                .andExpect(jsonPath("$.name").value("Matematicas"))
                .andExpect(jsonPath("$.description").value("Curso de matematicas"))
                .andExpect(jsonPath("$.hours").value(80))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        ArgumentCaptor<CreateSubjectCommand> captor =
                ArgumentCaptor.forClass(CreateSubjectCommand.class);
        verify(createUseCase).execute(captor.capture());
        assertThat(captor.getValue().schoolId().value()).isEqualTo(SCHOOL_ID);
        assertThat(captor.getValue().code()).isEqualTo("MAT-101");
        assertThat(captor.getValue().name()).isEqualTo("Matematicas");
        assertThat(captor.getValue().description()).isEqualTo("Curso de matematicas");
        assertThat(captor.getValue().hours()).isEqualTo(80);
    }

    @Test
    @DisplayName("POST should return 409 on duplicate code")
    void postShouldReturn409OnDuplicate() throws Exception {
        when(createUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("already exists"));

        CreateSubjectRequest request = new CreateSubjectRequest("MAT-101", "Matematicas", "Desc", 80);

        mockMvc.perform(post("/api/v1/schools/{schoolId}/subjects", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST should return 404 when school not found")
    void postShouldReturn404WhenSchoolNotFound() throws Exception {
        when(createUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("School not found"));

        CreateSubjectRequest request = new CreateSubjectRequest("MAT-101", "Matematicas", "Desc", 80);

        mockMvc.perform(post("/api/v1/schools/{schoolId}/subjects", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET by id should return 200")
    void getByIdShouldReturn200() throws Exception {
        SubjectResult result = buildResult(SUBJECT_ID, "MAT-102", "Fisica", "Curso de fisica", 60, "ACTIVE");
        when(getUseCase.execute(any())).thenReturn(result);

        mockMvc.perform(get("/api/v1/schools/{schoolId}/subjects/{id}", SCHOOL_ID, SUBJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SUBJECT_ID))
                .andExpect(jsonPath("$.code").value("MAT-102"))
                .andExpect(jsonPath("$.name").value("Fisica"))
                .andExpect(jsonPath("$.hours").value(60));
    }

    @Test
    @DisplayName("GET by id should return 404 when not found")
    void getByIdShouldReturn404WhenNotFound() throws Exception {
        when(getUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("not found"));

        mockMvc.perform(get("/api/v1/schools/{schoolId}/subjects/{id}", SCHOOL_ID, SUBJECT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET list by schoolId should return 200 with list")
    void listBySchoolIdShouldReturn200WithList() throws Exception {
        SubjectResult r1 = buildResult("id-1", "MAT-101", "Matematicas", "Desc 1", 80, "ACTIVE");
        SubjectResult r2 = buildResult("id-2", "MAT-102", "Fisica", "Desc 2", 60, "ACTIVE");
        when(listUseCase.execute(any())).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/schools/{schoolId}/subjects", SCHOOL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("MAT-101"))
                .andExpect(jsonPath("$[1].code").value("MAT-102"));
    }

    @Test
    @DisplayName("GET list by schoolId should return 200 with empty list")
    void listBySchoolIdShouldReturnEmptyList() throws Exception {
        when(listUseCase.execute(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/schools/{schoolId}/subjects", SCHOOL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("PUT should update and return 200")
    void putShouldUpdateAndReturn200() throws Exception {
        SubjectResult result = buildResult(SUBJECT_ID, "MAT-101", "Matematicas Avanzada", "Desc avanzada", 100, "ACTIVE");
        when(updateUseCase.execute(any())).thenReturn(result);

        UpdateSubjectRequest request = new UpdateSubjectRequest("MAT-101", "Matematicas Avanzada", "Desc avanzada", 100);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/subjects/{id}", SCHOOL_ID, SUBJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Matematicas Avanzada"))
                .andExpect(jsonPath("$.hours").value(100));
    }

    @Test
    @DisplayName("PUT should return 409 on duplicate code")
    void putShouldReturn409OnDuplicate() throws Exception {
        when(updateUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("already exists"));

        UpdateSubjectRequest request = new UpdateSubjectRequest("MAT-999", "X", "Desc", 80);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/subjects/{id}", SCHOOL_ID, SUBJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT should return 404 when not found")
    void putShouldReturn404WhenNotFound() throws Exception {
        when(updateUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("Subject not found"));

        UpdateSubjectRequest request = new UpdateSubjectRequest("MAT-101", "X", "Desc", 80);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/subjects/{id}", SCHOOL_ID, SUBJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT should return 422 when subject is inactive")
    void putShouldReturn422WhenInactive() throws Exception {
        when(updateUseCase.execute(any()))
                .thenThrow(new IllegalStateException("inactive"));

        UpdateSubjectRequest request = new UpdateSubjectRequest("MAT-101", "X", "Desc", 80);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/subjects/{id}", SCHOOL_ID, SUBJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH deactivate should return 200")
    void patchDeactivateShouldReturn200() throws Exception {
        SubjectResult result = buildResult(SUBJECT_ID, "MAT-101", "Matematicas", "Desc", 80, "INACTIVE");
        when(deactivateUseCase.execute(any())).thenReturn(result);

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/subjects/{id}/deactivate", SCHOOL_ID, SUBJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("PATCH deactivate should return 404 when not found")
    void patchDeactivateShouldReturn404WhenNotFound() throws Exception {
        when(deactivateUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("not found"));

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/subjects/{id}/deactivate", SCHOOL_ID, SUBJECT_ID))
                .andExpect(status().isNotFound());
    }

    private SubjectResult buildResult(String id, String code, String name, String description, int hours, String status) {
        return new SubjectResult(id, SCHOOL_ID, code, name, description, hours, status, NOW, NOW);
    }
}
