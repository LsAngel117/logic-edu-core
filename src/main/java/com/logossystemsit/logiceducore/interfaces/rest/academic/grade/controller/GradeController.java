package com.logossystemsit.logiceducore.interfaces.rest.academic.grade.controller;

import com.logossystemsit.logiceducore.application.academic.grade.dto.command.RegisterGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.command.UpdateGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.application.academic.grade.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.grade.dto.request.RegisterGradeRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.grade.dto.request.UpdateGradeRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.grade.dto.response.GradeResponse;

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
@RequestMapping("/api/v1/assessments/{assessmentId}/grades")
@Tag(name = "Calificaciones", description = "Registro de notas y resultados académicos")
public class GradeController {

    private final RegisterGradeUseCase registerUseCase;
    private final GetGradeUseCase getUseCase;
    private final ListGradesByAssessmentUseCase listUseCase;
    private final UpdateGradeUseCase updateUseCase;

    public GradeController(
            RegisterGradeUseCase registerUseCase,
            GetGradeUseCase getUseCase,
            ListGradesByAssessmentUseCase listUseCase,
            UpdateGradeUseCase updateUseCase) {
        this.registerUseCase = registerUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Registrar calificación", description = "Asigna una nota a un estudiante en una evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GradeResponse> register(
            @PathVariable String assessmentId,
            @RequestBody RegisterGradeRequest request,
            Principal principal) {
        try {
            RegisterGradeCommand command = new RegisterGradeCommand(
                    new AssessmentId(assessmentId),
                    new UserId(request.studentId()),
                    request.value(),
                    new UserId(principal.getName())
            );
            GradeResult result = registerUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GradeResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener calificación", description = "Consulta una calificación por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GradeResponse> getById(
            @PathVariable String assessmentId,
            @PathVariable String id) {
        try {
            GradeResult result = getUseCase.execute(new GradeId(id));
            return ResponseEntity.ok(GradeResponse.from(result));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar calificaciones", description = "Obtiene las calificaciones de una evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<GradeResponse>> listByAssessment(@PathVariable String assessmentId) {
        List<GradeResult> results = listUseCase.execute(new AssessmentId(assessmentId));
        List<GradeResponse> responses = results.stream()
                .map(GradeResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Actualizar calificación", description = "Modifica la nota de un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GradeResponse> update(
            @PathVariable String assessmentId,
            @PathVariable String studentId,
            @RequestBody UpdateGradeRequest request,
            Principal principal) {
        try {
            UpdateGradeCommand command = new UpdateGradeCommand(
                    new AssessmentId(assessmentId),
                    new UserId(studentId),
                    request.value(),
                    new UserId(principal.getName())
            );
            GradeResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(GradeResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }
}
