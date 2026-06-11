package com.logossystemsit.logiceducore.interfaces.rest.branch.controller;

import com.logossystemsit.logiceducore.application.branch.dto.command.CreateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.command.UpdateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.*;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.shared.valueobject.City;
import com.logossystemsit.logiceducore.shared.valueobject.Country;
import com.logossystemsit.logiceducore.interfaces.rest.branch.dto.request.CreateBranchRequest;
import com.logossystemsit.logiceducore.interfaces.rest.branch.dto.request.UpdateBranchRequest;
import com.logossystemsit.logiceducore.interfaces.rest.branch.dto.response.BranchResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schools/{schoolId}/branches")
@Tag(name = "Sedes", description = "Administración de sedes por institución")
public class BranchController {

    private final CreateBranchUseCase createBranchUseCase;
    private final GetBranchUseCase getBranchUseCase;
    private final ListBranchesBySchoolUseCase listBranchesBySchoolUseCase;
    private final UpdateBranchUseCase updateBranchUseCase;
    private final DeactivateBranchUseCase deactivateBranchUseCase;

    public BranchController(CreateBranchUseCase createBranchUseCase,
                            GetBranchUseCase getBranchUseCase,
                            ListBranchesBySchoolUseCase listBranchesBySchoolUseCase,
                            UpdateBranchUseCase updateBranchUseCase,
                            DeactivateBranchUseCase deactivateBranchUseCase) {
        this.createBranchUseCase = createBranchUseCase;
        this.getBranchUseCase = getBranchUseCase;
        this.listBranchesBySchoolUseCase = listBranchesBySchoolUseCase;
        this.updateBranchUseCase = updateBranchUseCase;
        this.deactivateBranchUseCase = deactivateBranchUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear sede", description = "Registra una nueva sede para una institución")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BranchResponse> createBranch(@PathVariable String schoolId,
                                                          @RequestBody CreateBranchRequest request) {
        try {
            CreateBranchCommand command = mapToCreateCommand(schoolId, request);
            BranchResult result = createBranchUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sede", description = "Consulta una sede por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BranchResponse> getBranch(@PathVariable String schoolId,
                                                       @PathVariable String id) {
        try {
            SchoolId sId = new SchoolId(schoolId);
            BranchId bId = BranchId.of(id);
            BranchResult result = getBranchUseCase.execute(sId, bId);
            return ResponseEntity.ok(toResponse(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar sedes", description = "Obtiene todas las sedes de una institución")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<BranchResponse>> listBranches(@PathVariable String schoolId) {
        SchoolId sId = new SchoolId(schoolId);
        List<BranchResult> results = listBranchesBySchoolUseCase.execute(sId);
        List<BranchResponse> responses = results.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sede", description = "Modifica los datos de una sede")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BranchResponse> updateBranch(@PathVariable String schoolId,
                                                          @PathVariable String id,
                                                          @RequestBody UpdateBranchRequest request) {
        try {
            UpdateBranchCommand command = mapToUpdateCommand(schoolId, id, request);
            BranchResult result = updateBranchUseCase.execute(command);
            return ResponseEntity.ok(toResponse(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar sede", description = "Desactiva una sede")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BranchResponse> deactivateBranch(@PathVariable String schoolId,
                                                              @PathVariable String id) {
        try {
            SchoolId sId = new SchoolId(schoolId);
            BranchId bId = BranchId.of(id);
            BranchResult result = deactivateBranchUseCase.execute(sId, bId);
            return ResponseEntity.ok(toResponse(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CreateBranchCommand mapToCreateCommand(String schoolId, CreateBranchRequest r) {
        SchoolId sId = new SchoolId(schoolId);
        BranchId bId = BranchId.generate();
        BranchName name = BranchName.of(r.name());
        BranchCode code = BranchCode.of(r.code());
        BranchShortName shortName = BranchShortName.of(r.shortName());
        BranchDescription description = r.description() != null && !r.description().isBlank()
                ? BranchDescription.of(r.description())
                : BranchDescription.empty();
        BranchEmail email = r.email() != null && !r.email().isBlank()
                ? BranchEmail.of(r.email())
                : null;
        BranchPhone phone = r.phone() != null && !r.phone().isBlank()
                ? BranchPhone.of(r.phone())
                : null;
        BranchAddress address = r.address() != null && !r.address().isBlank()
                ? BranchAddress.of(r.address())
                : BranchAddress.empty();
        City city = new City(r.city());
        Country country = new Country(r.country());
        BranchType type = BranchType.valueOf(r.type().toUpperCase());

        return new CreateBranchCommand(bId, sId, name, code, shortName, description, email, phone, address, city, country, type);
    }

    private UpdateBranchCommand mapToUpdateCommand(String schoolId, String branchId, UpdateBranchRequest r) {
        SchoolId sId = new SchoolId(schoolId);
        BranchId bId = BranchId.of(branchId);
        BranchName name = BranchName.of(r.name());
        BranchCode code = BranchCode.of(r.code());
        BranchShortName shortName = BranchShortName.of(r.shortName());
        BranchDescription description = r.description() != null && !r.description().isBlank()
                ? BranchDescription.of(r.description())
                : BranchDescription.empty();
        BranchEmail email = r.email() != null && !r.email().isBlank()
                ? BranchEmail.of(r.email())
                : null;
        BranchPhone phone = r.phone() != null && !r.phone().isBlank()
                ? BranchPhone.of(r.phone())
                : null;
        BranchAddress address = r.address() != null && !r.address().isBlank()
                ? BranchAddress.of(r.address())
                : BranchAddress.empty();
        City city = new City(r.city());
        Country country = new Country(r.country());
        BranchType type = BranchType.valueOf(r.type().toUpperCase());

        return new UpdateBranchCommand(sId, bId, name, code, shortName, description, email, phone, address, city, country, type);
    }

    private BranchResponse toResponse(BranchResult result) {
        return new BranchResponse(
                result.id(),
                result.schoolId(),
                result.name(),
                result.code(),
                result.shortName(),
                result.description(),
                result.email(),
                result.phone(),
                result.address(),
                result.city(),
                result.country(),
                result.type(),
                result.status(),
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
