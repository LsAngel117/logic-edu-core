package com.logossystemsit.logiceducore.interfaces.rest.academic.enrollment.controller;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.command.EnrollStudentCommand;
import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentId;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.enrollment.dto.request.EnrollStudentRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.enrollment.dto.response.EnrollmentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Tag(name = "Matrículas", description = "Inscripción de estudiantes en grupos")
public class EnrollmentController {

    private final EnrollStudentUseCase enrollUseCase;
    private final GetEnrollmentUseCase getUseCase;
    private final ListEnrollmentsByGroupUseCase listUseCase;
    private final DropEnrollmentUseCase dropUseCase;

    public EnrollmentController(
            EnrollStudentUseCase enrollUseCase,
            GetEnrollmentUseCase getUseCase,
            ListEnrollmentsByGroupUseCase listUseCase,
            DropEnrollmentUseCase dropUseCase) {
        this.enrollUseCase = enrollUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.dropUseCase = dropUseCase;
    }

    @PostMapping("/api/v1/enrollments")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Matricular estudiante", description = "Inscribe a un estudiante en un grupo, validando capacidad y reglas de negocio")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EnrollmentResponse> enroll(
            @RequestBody EnrollStudentRequest request) {
        try {
            EnrollStudentCommand command = new EnrollStudentCommand(
                    new UserId(request.userId()),
                    new GroupId(request.groupId())
            );
            EnrollmentResult result = enrollUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(EnrollmentResponse.from(result));
        } catch (OptimisticLockingFailureException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("already enrolled")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/api/v1/enrollments/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener matrícula", description = "Consulta una matrícula por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EnrollmentResponse> getById(@PathVariable String id) {
        try {
            EnrollmentId enrollmentId = new EnrollmentId(id);
            EnrollmentResult result = getUseCase.execute(enrollmentId);
            return ResponseEntity.ok(EnrollmentResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/api/v1/groups/{groupId}/enrollments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar matrículas", description = "Obtiene las matrículas de un grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<EnrollmentResponse>> listByGroup(
            @PathVariable String groupId) {
        try {
            GroupId gid = new GroupId(groupId);
            List<EnrollmentResult> results = listUseCase.execute(gid);
            List<EnrollmentResponse> responses = results.stream()
                    .map(EnrollmentResponse::from)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/api/v1/enrollments/{id}/drop")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Anular matrícula", description = "Cancela la matrícula de un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EnrollmentResponse> drop(@PathVariable String id) {
        try {
            EnrollmentId enrollmentId = new EnrollmentId(id);
            EnrollmentResult result = dropUseCase.execute(enrollmentId);
            return ResponseEntity.ok(EnrollmentResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
