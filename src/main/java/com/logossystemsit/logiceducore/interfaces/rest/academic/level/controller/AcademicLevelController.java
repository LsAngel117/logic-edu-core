package com.logossystemsit.logiceducore.interfaces.rest.academic.level.controller;

import com.logossystemsit.logiceducore.application.academic.level.dto.command.CreateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.command.UpdateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.request.CreateAcademicLevelRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.request.UpdateAcademicLevelRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.response.AcademicLevelResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schools/{schoolId}/levels")
@Tag(name = "Niveles Académicos", description = "Gestión de niveles o años de formación")
public class AcademicLevelController {

    private final CreateAcademicLevelUseCase createUseCase;
    private final GetAcademicLevelUseCase getUseCase;
    private final ListAcademicLevelsBySchoolUseCase listUseCase;
    private final UpdateAcademicLevelUseCase updateUseCase;
    private final DeactivateAcademicLevelUseCase deactivateUseCase;

    public AcademicLevelController(
            CreateAcademicLevelUseCase createUseCase,
            GetAcademicLevelUseCase getUseCase,
            ListAcademicLevelsBySchoolUseCase listUseCase,
            UpdateAcademicLevelUseCase updateUseCase,
            DeactivateAcademicLevelUseCase deactivateUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
        this.deactivateUseCase = deactivateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Crear nivel", description = "Crea un nuevo nivel o año académico")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicLevelResponse> create(
            @PathVariable String schoolId,
            @RequestBody CreateAcademicLevelRequest request) {
        try {
            CreateAcademicLevelCommand command = mapToCreateCommand(schoolId, request);
            AcademicLevelResult result = createUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AcademicLevelResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener nivel", description = "Consulta un nivel académico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicLevelResponse> getById(@PathVariable String id) {
        try {
            AcademicLevelId levelId = new AcademicLevelId(id);
            AcademicLevelResult result = getUseCase.execute(levelId);
            return ResponseEntity.ok(AcademicLevelResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar niveles", description = "Obtiene los niveles de una institución")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<AcademicLevelResponse>> listBySchool(@PathVariable String schoolId) {
        SchoolId sid = new SchoolId(schoolId);
        List<AcademicLevelResult> results = listUseCase.execute(sid);
        List<AcademicLevelResponse> responses = results.stream()
                .map(AcademicLevelResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Actualizar nivel", description = "Modifica un nivel académico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicLevelResponse> update(
            @PathVariable String schoolId,
            @PathVariable String id,
            @RequestBody UpdateAcademicLevelRequest request) {
        try {
            UpdateAcademicLevelCommand command = mapToUpdateCommand(schoolId, id, request);
            AcademicLevelResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(AcademicLevelResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Desactivar nivel", description = "Desactiva un nivel académico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicLevelResponse> deactivate(@PathVariable String id) {
        try {
            AcademicLevelId levelId = new AcademicLevelId(id);
            AcademicLevelResult result = deactivateUseCase.execute(levelId);
            return ResponseEntity.ok(AcademicLevelResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CreateAcademicLevelCommand mapToCreateCommand(String schoolId, CreateAcademicLevelRequest r) {
        return new CreateAcademicLevelCommand(
                AcademicLevelId.generate(),
                new SchoolId(schoolId),
                r.name(),
                r.number()
        );
    }

    private UpdateAcademicLevelCommand mapToUpdateCommand(String schoolId, String id, UpdateAcademicLevelRequest r) {
        return new UpdateAcademicLevelCommand(
                new AcademicLevelId(id),
                new SchoolId(schoolId),
                r.name(),
                r.number()
        );
    }
}
