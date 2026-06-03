package com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.controller;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.CreateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.UpdateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.application.academic.assessment.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.AssessmentType;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.dto.request.CreateAssessmentRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.dto.request.UpdateAssessmentRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.dto.response.AssessmentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/assessments")
@Tag(name = "Evaluaciones", description = "Creación y gestión de actividades evaluables")
public class AssessmentController {

    private final CreateAssessmentUseCase createUseCase;
    private final GetAssessmentUseCase getUseCase;
    private final ListAssessmentsByGroupUseCase listUseCase;
    private final UpdateAssessmentUseCase updateUseCase;
    private final DeleteAssessmentUseCase deleteUseCase;

    public AssessmentController(
            CreateAssessmentUseCase createUseCase,
            GetAssessmentUseCase getUseCase,
            ListAssessmentsByGroupUseCase listUseCase,
            UpdateAssessmentUseCase updateUseCase,
            DeleteAssessmentUseCase deleteUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Crear evaluación", description = "Crea una actividad evaluable para un grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AssessmentResponse> create(
            @PathVariable String groupId,
            @RequestBody CreateAssessmentRequest request,
            Principal principal) {
        try {
            CreateAssessmentCommand command = new CreateAssessmentCommand(
                    new GroupId(groupId),
                    request.name(),
                    AssessmentType.valueOf(request.type()),
                    request.weight(),
                    request.maxScore(),
                    request.evaluationPeriodId() != null
                            ? new EvaluationPeriodId(request.evaluationPeriodId())
                            : null,
                    new UserId(principal.getName())
            );
            AssessmentResult result = createUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AssessmentResponse.from(result));
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener evaluación", description = "Consulta una evaluación por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AssessmentResponse> getById(
            @PathVariable String groupId,
            @PathVariable String id) {
        try {
            AssessmentResult result = getUseCase.execute(new AssessmentId(id));
            return ResponseEntity.ok(AssessmentResponse.from(result));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar evaluaciones", description = "Obtiene las evaluaciones de un grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<AssessmentResponse>> listByGroup(@PathVariable String groupId) {
        List<AssessmentResult> results = listUseCase.execute(new GroupId(groupId));
        List<AssessmentResponse> responses = results.stream()
                .map(AssessmentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Actualizar evaluación", description = "Modifica una actividad evaluable")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AssessmentResponse> update(
            @PathVariable String groupId,
            @PathVariable String id,
            @RequestBody UpdateAssessmentRequest request,
            Principal principal) {
        try {
            UpdateAssessmentCommand command = new UpdateAssessmentCommand(
                    new AssessmentId(id),
                    new GroupId(groupId),
                    request.name(),
                    AssessmentType.valueOf(request.type()),
                    request.weight(),
                    request.maxScore(),
                    request.evaluationPeriodId() != null
                            ? new EvaluationPeriodId(request.evaluationPeriodId())
                            : null,
                    new UserId(principal.getName())
            );
            AssessmentResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(AssessmentResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Eliminar evaluación", description = "Elimina una evaluación si no tiene calificaciones registradas")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operación exitosa sin contenido"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> delete(
            @PathVariable String groupId,
            @PathVariable String id,
            Principal principal) {
        try {
            deleteUseCase.execute(
                    new AssessmentId(id),
                    new GroupId(groupId),
                    new UserId(principal.getName())
            );
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("has grades")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }
}
