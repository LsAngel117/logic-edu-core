package com.logossystemsit.logiceducore.interfaces.rest.branch.controller;

import com.logossystemsit.logiceducore.application.branch.dto.command.CreateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.command.UpdateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.*;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.interfaces.rest.branch.dto.request.CreateBranchRequest;
import com.logossystemsit.logiceducore.interfaces.rest.branch.dto.request.UpdateBranchRequest;

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
class BranchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateBranchUseCase createBranchUseCase;

    @Mock
    private GetBranchUseCase getBranchUseCase;

    @Mock
    private ListBranchesBySchoolUseCase listBranchesBySchoolUseCase;

    @Mock
    private UpdateBranchUseCase updateBranchUseCase;

    @Mock
    private DeactivateBranchUseCase deactivateBranchUseCase;

    @InjectMocks
    private BranchController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final String SCHOOL_ID = "school-1";
    private static final String BRANCH_ID = "branch-1";

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createBranchShouldReturn201WithBranchResponse() throws Exception {
        CreateBranchRequest request = validCreateBranchRequest();
        BranchResult result = buildResult("branch-1", "Sede Norte", "SN-001", "MAIN", "ACTIVE");

        when(createBranchUseCase.execute(any(CreateBranchCommand.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/schools/{schoolId}/branches", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("branch-1"))
                .andExpect(jsonPath("$.name").value("Sede Norte"))
                .andExpect(jsonPath("$.code").value("SN-001"))
                .andExpect(jsonPath("$.type").value("MAIN"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.schoolId").value(SCHOOL_ID));
    }

    @Test
    void createBranchWithDuplicateNameShouldReturn409() throws Exception {
        CreateBranchRequest request = validCreateBranchRequest();

        when(createBranchUseCase.execute(any(CreateBranchCommand.class)))
                .thenThrow(new IllegalArgumentException("Branch name already exists within this school"));

        mockMvc.perform(post("/api/v1/schools/{schoolId}/branches", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void createBranchWithDuplicateMainShouldReturn409() throws Exception {
        CreateBranchRequest request = validCreateBranchRequest();

        when(createBranchUseCase.execute(any(CreateBranchCommand.class)))
                .thenThrow(new IllegalArgumentException("A MAIN branch already exists for this school"));

        mockMvc.perform(post("/api/v1/schools/{schoolId}/branches", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void createBranchWithInactiveSchoolShouldReturn422() throws Exception {
        CreateBranchRequest request = validCreateBranchRequest();

        when(createBranchUseCase.execute(any(CreateBranchCommand.class)))
                .thenThrow(new IllegalStateException("Cannot create branch in an inactive school"));

        mockMvc.perform(post("/api/v1/schools/{schoolId}/branches", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createBranchWithVirtualAndAddressShouldReturn422() throws Exception {
        CreateBranchRequest request = new CreateBranchRequest(
                "Sede Virtual", "SV-001", "S.Virtual",
                "Desc virtual", "virtual@branch.edu", "+571234567", "Calle 123"
        );

        when(createBranchUseCase.execute(any(CreateBranchCommand.class)))
                .thenThrow(new IllegalStateException("Virtual branch cannot have a physical address"));

        mockMvc.perform(post("/api/v1/schools/{schoolId}/branches", SCHOOL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getBranchShouldReturn200WithBranchResponse() throws Exception {
        BranchResult result = buildResult("branch-1", "Sede Norte", "SN-001", "MAIN", "ACTIVE");

        when(getBranchUseCase.execute(any(SchoolId.class), any(BranchId.class))).thenReturn(result);

        mockMvc.perform(get("/api/v1/schools/{schoolId}/branches/{id}", SCHOOL_ID, BRANCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("branch-1"))
                .andExpect(jsonPath("$.name").value("Sede Norte"));
    }

    @Test
    void getBranchWithUnknownIdShouldReturn404() throws Exception {
        when(getBranchUseCase.execute(any(SchoolId.class), any(BranchId.class)))
                .thenThrow(new IllegalArgumentException("Branch not found"));

        mockMvc.perform(get("/api/v1/schools/{schoolId}/branches/{id}", SCHOOL_ID, "unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listBranchesShouldReturn200WithBranchList() throws Exception {
        BranchResult b1 = buildResult("branch-1", "Sede A", "SA-001", "MAIN", "ACTIVE");
        BranchResult b2 = buildResult("branch-2", "Sede B", "SB-002", "SECONDARY", "ACTIVE");

        when(listBranchesBySchoolUseCase.execute(any(SchoolId.class))).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/v1/schools/{schoolId}/branches", SCHOOL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Sede A"))
                .andExpect(jsonPath("$[1].name").value("Sede B"));
    }

    @Test
    void listBranchesWithEmptyListShouldReturn200() throws Exception {
        when(listBranchesBySchoolUseCase.execute(any(SchoolId.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/schools/{schoolId}/branches", SCHOOL_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updateBranchShouldReturn200WithUpdatedBranch() throws Exception {
        UpdateBranchRequest request = validUpdateBranchRequest();
        BranchResult result = buildResult("branch-1", "Sede Norte Actualizada", "SNU-001", "SECONDARY", "ACTIVE");

        when(updateBranchUseCase.execute(any(UpdateBranchCommand.class))).thenReturn(result);

        mockMvc.perform(put("/api/v1/schools/{schoolId}/branches/{id}", SCHOOL_ID, BRANCH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sede Norte Actualizada"))
                .andExpect(jsonPath("$.code").value("SNU-001"));
    }

    @Test
    void updateInactiveBranchShouldReturn422() throws Exception {
        UpdateBranchRequest request = validUpdateBranchRequest();

        when(updateBranchUseCase.execute(any(UpdateBranchCommand.class)))
                .thenThrow(new IllegalStateException("Cannot modify an inactive branch"));

        mockMvc.perform(put("/api/v1/schools/{schoolId}/branches/{id}", SCHOOL_ID, BRANCH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateBranchNotFoundShouldReturn404() throws Exception {
        UpdateBranchRequest request = validUpdateBranchRequest();

        when(updateBranchUseCase.execute(any(UpdateBranchCommand.class)))
                .thenThrow(new IllegalArgumentException("Branch not found"));

        mockMvc.perform(put("/api/v1/schools/{schoolId}/branches/{id}", SCHOOL_ID, "unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateBranchShouldReturn200() throws Exception {
        BranchResult result = buildResult("branch-1", "Sede Norte", "SN-001", "SECONDARY", "INACTIVE");

        when(deactivateBranchUseCase.execute(any(SchoolId.class), any(BranchId.class))).thenReturn(result);

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/branches/{id}/deactivate", SCHOOL_ID, BRANCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void deactivateBranchNotFoundShouldReturn404() throws Exception {
        when(deactivateBranchUseCase.execute(any(SchoolId.class), any(BranchId.class)))
                .thenThrow(new IllegalArgumentException("Branch not found"));

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/branches/{id}/deactivate", SCHOOL_ID, "unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateBranchWithActiveSecondariesShouldReturn422() throws Exception {
        when(deactivateBranchUseCase.execute(any(SchoolId.class), any(BranchId.class)))
                .thenThrow(new IllegalStateException(
                        "Cannot deactivate the main branch while there are active secondary branches"));

        mockMvc.perform(patch("/api/v1/schools/{schoolId}/branches/{id}/deactivate", SCHOOL_ID, BRANCH_ID))
                .andExpect(status().isUnprocessableEntity());
    }

    private static CreateBranchRequest validCreateBranchRequest() {
        return new CreateBranchRequest(
                "Sede Norte", "SN-001", "S.Norte",
                "Sede principal norte", "norte@branch.edu", "+571234567", "Calle 123 #45-67"
        );
    }

    private static UpdateBranchRequest validUpdateBranchRequest() {
        return new UpdateBranchRequest(
                "Sede Norte Actualizada", "SNU-001", "S.NorteAct",
                "Actualizada 2025", "nueva@branch.edu", "+579876543", "Carrera 45 #67-89"
        );
    }

    private BranchResult buildResult(String id, String name, String code, String type, String status) {
        return new BranchResult(
                id, SCHOOL_ID, name, code, "Short",
                "Description", "branch@school.edu", "+571234567", "Calle 1",
                type, status, NOW, NOW
        );
    }
}
