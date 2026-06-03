package com.logossystemsit.logiceducore.interfaces.rest.academic.subject.controller;

import com.logossystemsit.logiceducore.application.academic.subject.dto.command.CreateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.command.UpdateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.request.CreateSubjectRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.request.UpdateSubjectRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.response.SubjectResponse;

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
@RequestMapping("/api/v1/schools/{schoolId}/subjects")
@Tag(name = "Materias", description = "Catálogo de materias y asignaturas")
public class SubjectController {

    private final CreateSubjectUseCase createUseCase;
    private final GetSubjectUseCase getUseCase;
    private final ListSubjectsBySchoolUseCase listUseCase;
    private final UpdateSubjectUseCase updateUseCase;
    private final DeactivateSubjectUseCase deactivateUseCase;

    public SubjectController(
            CreateSubjectUseCase createUseCase,
            GetSubjectUseCase getUseCase,
            ListSubjectsBySchoolUseCase listUseCase,
            UpdateSubjectUseCase updateUseCase,
            DeactivateSubjectUseCase deactivateUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
        this.deactivateUseCase = deactivateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Crear materia", description = "Registra una nueva materia en el catálogo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SubjectResponse> create(
            @PathVariable String schoolId,
            @RequestBody CreateSubjectRequest request) {
        try {
            CreateSubjectCommand command = mapToCreateCommand(schoolId, request);
            SubjectResult result = createUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(SubjectResponse.from(result));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("School not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener materia", description = "Consulta una materia por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SubjectResponse> getById(@PathVariable String id) {
        try {
            SubjectId subjectId = new SubjectId(id);
            SubjectResult result = getUseCase.execute(subjectId);
            return ResponseEntity.ok(SubjectResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar materias", description = "Obtiene las materias de una institución")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<SubjectResponse>> listBySchool(@PathVariable String schoolId) {
        SchoolId sid = new SchoolId(schoolId);
        List<SubjectResult> results = listUseCase.execute(sid);
        List<SubjectResponse> responses = results.stream()
                .map(SubjectResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Actualizar materia", description = "Modifica los datos de una materia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SubjectResponse> update(
            @PathVariable String schoolId,
            @PathVariable String id,
            @RequestBody UpdateSubjectRequest request) {
        try {
            UpdateSubjectCommand command = mapToUpdateCommand(schoolId, id, request);
            SubjectResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(SubjectResponse.from(result));
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
    @Operation(summary = "Desactivar materia", description = "Desactiva una materia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SubjectResponse> deactivate(@PathVariable String id) {
        try {
            SubjectId subjectId = new SubjectId(id);
            SubjectResult result = deactivateUseCase.execute(subjectId);
            return ResponseEntity.ok(SubjectResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CreateSubjectCommand mapToCreateCommand(String schoolId, CreateSubjectRequest r) {
        return new CreateSubjectCommand(
                SubjectId.generate(),
                new SchoolId(schoolId),
                r.code(),
                r.name(),
                r.description(),
                r.hours()
        );
    }

    private UpdateSubjectCommand mapToUpdateCommand(String schoolId, String id, UpdateSubjectRequest r) {
        return new UpdateSubjectCommand(
                new SubjectId(id),
                new SchoolId(schoolId),
                r.code(),
                r.name(),
                r.description(),
                r.hours()
        );
    }
}
