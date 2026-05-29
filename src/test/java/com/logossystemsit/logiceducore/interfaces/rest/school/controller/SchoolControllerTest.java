package com.logossystemsit.logiceducore.interfaces.rest.school.controller;

import com.logossystemsit.logiceducore.application.school.dto.command.CreateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.command.UpdateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.interfaces.rest.school.dto.request.CreateSchoolRequest;
import com.logossystemsit.logiceducore.interfaces.rest.school.dto.request.UpdateSchoolRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
class SchoolControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateSchoolUseCase createSchoolUseCase;

    @Mock
    private GetSchoolUseCase getSchoolUseCase;

    @Mock
    private ListSchoolsUseCase listSchoolsUseCase;

    @Mock
    private UpdateSchoolUseCase updateSchoolUseCase;

    @Mock
    private DeactivateSchoolUseCase deactivateSchoolUseCase;

    @InjectMocks
    private SchoolController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createSchoolShouldReturn201WithSchoolResponse() throws Exception {
        CreateSchoolRequest request = validCreateSchoolRequest();
        SchoolResult result = buildResult("school-1", "Colegio Andino", "CA-001", "ACTIVE");

        when(createSchoolUseCase.execute(any(CreateSchoolCommand.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("school-1"))
                .andExpect(jsonPath("$.name").value("Colegio Andino"))
                .andExpect(jsonPath("$.code").value("CA-001"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createSchoolWithDuplicateNameShouldReturn409() throws Exception {
        CreateSchoolRequest request = validCreateSchoolRequest();

        when(createSchoolUseCase.execute(any(CreateSchoolCommand.class)))
                .thenThrow(new IllegalArgumentException("School name already exists"));

        mockMvc.perform(post("/api/v1/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getSchoolShouldReturn200WithSchoolResponse() throws Exception {
        SchoolId schoolId = new SchoolId("school-1");
        SchoolResult result = buildResult("school-1", "Colegio Andino", "CA-001", "ACTIVE");

        when(getSchoolUseCase.execute(any(SchoolId.class))).thenReturn(result);

        mockMvc.perform(get("/api/v1/schools/{id}", "school-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("school-1"))
                .andExpect(jsonPath("$.name").value("Colegio Andino"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getSchoolWithUnknownIdShouldReturn404() throws Exception {
        when(getSchoolUseCase.execute(any(SchoolId.class)))
                .thenThrow(new IllegalArgumentException("School not found"));

        mockMvc.perform(get("/api/v1/schools/{id}", "unknown-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listSchoolsShouldReturn200WithSchoolList() throws Exception {
        SchoolResult s1 = buildResult("school-1", "Colegio A", "CA-001", "ACTIVE");
        SchoolResult s2 = buildResult("school-2", "Colegio B", "CB-002", "ACTIVE");

        when(listSchoolsUseCase.execute()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/v1/schools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Colegio A"))
                .andExpect(jsonPath("$[1].name").value("Colegio B"));
    }

    @Test
    void updateSchoolShouldReturn200WithUpdatedSchool() throws Exception {
        UpdateSchoolRequest request = validUpdateSchoolRequest();
        SchoolResult result = buildResult("school-1", "Colegio Andino Nueva", "CA-002", "ACTIVE");

        when(updateSchoolUseCase.execute(any(UpdateSchoolCommand.class))).thenReturn(result);

        mockMvc.perform(put("/api/v1/schools/{id}", "school-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Colegio Andino Nueva"))
                .andExpect(jsonPath("$.code").value("CA-002"));
    }

    @Test
    void updateInactiveSchoolShouldReturn422() throws Exception {
        UpdateSchoolRequest request = validUpdateSchoolRequest();

        when(updateSchoolUseCase.execute(any(UpdateSchoolCommand.class)))
                .thenThrow(new IllegalStateException("Cannot modify an inactive school"));

        mockMvc.perform(put("/api/v1/schools/{id}", "school-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deactivateSchoolShouldReturn200() throws Exception {
        SchoolResult result = buildResult("school-1", "Colegio Andino", "CA-001", "INACTIVE");

        when(deactivateSchoolUseCase.execute(any(SchoolId.class))).thenReturn(result);

        mockMvc.perform(patch("/api/v1/schools/{id}/deactivate", "school-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void deactivateSchoolNotFoundShouldReturn404() throws Exception {
        when(deactivateSchoolUseCase.execute(any(SchoolId.class)))
                .thenThrow(new IllegalArgumentException("School not found"));

        mockMvc.perform(patch("/api/v1/schools/{id}/deactivate", "unknown"))
                .andExpect(status().isNotFound());
    }

    private static CreateSchoolRequest validCreateSchoolRequest() {
        return new CreateSchoolRequest(
                "Colegio Andino",
                "CA-001",
                "Col.Andino",
                "Colegio bilingüe en Bogotá",
                "info@andino.edu",
                "+571234567",
                "Calle 123 #45-67"
        );
    }

    private static UpdateSchoolRequest validUpdateSchoolRequest() {
        return new UpdateSchoolRequest(
                "Colegio Andino Nueva",
                "CA-002",
                "Andino Nva",
                "Actualizado 2025",
                "nuevo@andino.edu",
                "+579876543",
                "Carrera 45 #67-89"
        );
    }

    private SchoolResult buildResult(String id, String name, String code, String status) {
        return new SchoolResult(
                id, name, code, "Short",
                "Description", "info@school.edu", "+571234567", "Address",
                status, NOW, NOW
        );
    }
}
