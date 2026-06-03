package com.logossystemsit.logiceducore.interfaces.rest.academic.structure.controller;

import com.logossystemsit.logiceducore.application.academic.structure.dto.command.CreateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.command.UpdateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.application.academic.structure.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.StructureType;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.request.CreateAcademicStructureRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.request.UpdateAcademicStructureRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.response.AcademicStructureResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/schools/{schoolId}/structures")
@Tag(name = "Estructura Académica", description = "Configuración académica de instituciones")
public class AcademicStructureController {

    private final CreateAcademicStructureUseCase createUseCase;
    private final GetAcademicStructureUseCase getUseCase;
    private final UpdateAcademicStructureUseCase updateUseCase;
    private final DeactivateAcademicStructureUseCase deactivateUseCase;

    public AcademicStructureController(
            CreateAcademicStructureUseCase createUseCase,
            GetAcademicStructureUseCase getUseCase,
            UpdateAcademicStructureUseCase updateUseCase,
            DeactivateAcademicStructureUseCase deactivateUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.updateUseCase = updateUseCase;
        this.deactivateUseCase = deactivateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Crear estructura académica", description = "Define la configuración académica de una institución")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicStructureResponse> create(
            @PathVariable String schoolId,
            @RequestBody CreateAcademicStructureRequest request) {
        try {
            CreateAcademicStructureCommand command = mapToCreateCommand(schoolId, request);
            AcademicStructureResult result = createUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AcademicStructureResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener estructura", description = "Consulta una estructura académica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicStructureResponse> getById(@PathVariable String id) {
        try {
            AcademicStructureId structureId = new AcademicStructureId(id);
            AcademicStructureResult result = getUseCase.execute(structureId);
            return ResponseEntity.ok(AcademicStructureResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener estructura activa", description = "Consulta la estructura académica activa de una institución")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicStructureResponse> getActive(@PathVariable String schoolId) {
        try {
            SchoolId sid = new SchoolId(schoolId);
            AcademicStructureResult result = getUseCase.findActiveBySchoolId(sid);
            return ResponseEntity.ok(AcademicStructureResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Actualizar estructura", description = "Modifica una estructura académica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicStructureResponse> update(
            @PathVariable String schoolId,
            @PathVariable String id,
            @RequestBody UpdateAcademicStructureRequest request) {
        try {
            UpdateAcademicStructureCommand command = mapToUpdateCommand(schoolId, id, request);
            AcademicStructureResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(AcademicStructureResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Desactivar estructura", description = "Desactiva una estructura académica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AcademicStructureResponse> deactivate(@PathVariable String id) {
        try {
            AcademicStructureId structureId = new AcademicStructureId(id);
            AcademicStructureResult result = deactivateUseCase.execute(structureId);
            return ResponseEntity.ok(AcademicStructureResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CreateAcademicStructureCommand mapToCreateCommand(String schoolId, CreateAcademicStructureRequest r) {
        return new CreateAcademicStructureCommand(
                AcademicStructureId.generate(),
                new SchoolId(schoolId),
                StructureType.valueOf(r.structureType().toUpperCase()),
                r.levelsCount(),
                r.periodsPerLevel(),
                r.evaluationPeriodsPerPeriod(),
                r.subjectsPerPeriod(),
                r.hoursPerSubject()
        );
    }

    private UpdateAcademicStructureCommand mapToUpdateCommand(String schoolId, String id, UpdateAcademicStructureRequest r) {
        return new UpdateAcademicStructureCommand(
                new AcademicStructureId(id),
                new SchoolId(schoolId),
                StructureType.valueOf(r.structureType().toUpperCase()),
                r.levelsCount(),
                r.periodsPerLevel(),
                r.evaluationPeriodsPerPeriod(),
                r.subjectsPerPeriod(),
                r.hoursPerSubject()
        );
    }
}
