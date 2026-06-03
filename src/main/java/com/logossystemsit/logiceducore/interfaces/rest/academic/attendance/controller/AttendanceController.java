package com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.controller;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.RegisterAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.UpdateAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.application.academic.attendance.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject.AttendanceStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.request.RegisterAttendanceRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.request.UpdateAttendanceRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.response.AttendanceResponse;

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
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/attendances")
@Tag(name = "Asistencia", description = "Registro y consulta de asistencia por sesión")
public class AttendanceController {

    private final RegisterAttendanceUseCase registerUseCase;
    private final GetAttendanceByDateUseCase getByDateUseCase;
    private final ListAttendancesByGroupUseCase listUseCase;
    private final UpdateAttendanceUseCase updateUseCase;

    public AttendanceController(
            RegisterAttendanceUseCase registerUseCase,
            GetAttendanceByDateUseCase getByDateUseCase,
            ListAttendancesByGroupUseCase listUseCase,
            UpdateAttendanceUseCase updateUseCase) {
        this.registerUseCase = registerUseCase;
        this.getByDateUseCase = getByDateUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Registrar asistencia", description = "Registra la asistencia de estudiantes en una sesión")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AttendanceResponse> register(
            @PathVariable String groupId,
            @RequestBody RegisterAttendanceRequest request,
            Principal principal) {
        try {
            RegisterAttendanceCommand command = new RegisterAttendanceCommand(
                    new GroupId(groupId),
                    request.date(),
                    new UserId(request.studentId()),
                    AttendanceStatus.valueOf(request.status()),
                    request.observations(),
                    new UserId(principal.getName())
            );
            AttendanceResult result = registerUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AttendanceResponse.from(result));
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/{date}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener asistencia", description = "Consulta la asistencia de una fecha específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<AttendanceResponse>> getByDate(
            @PathVariable String groupId,
            @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<AttendanceResult> results = getByDateUseCase.execute(new GroupId(groupId), localDate);
        List<AttendanceResponse> responses = results.stream()
                .map(AttendanceResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar asistencias", description = "Obtiene el historial de asistencia de un grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<AttendanceResponse>> listByGroup(@PathVariable String groupId) {
        List<AttendanceResult> results = listUseCase.execute(new GroupId(groupId));
        List<AttendanceResponse> responses = results.stream()
                .map(AttendanceResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{date}/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Actualizar asistencia", description = "Modifica el estado de asistencia de un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AttendanceResponse> update(
            @PathVariable String groupId,
            @PathVariable String date,
            @PathVariable String studentId,
            @RequestBody UpdateAttendanceRequest request,
            Principal principal) {
        try {
            UpdateAttendanceCommand command = new UpdateAttendanceCommand(
                    new GroupId(groupId),
                    LocalDate.parse(date),
                    new UserId(studentId),
                    AttendanceStatus.valueOf(request.status()),
                    request.observations(),
                    new UserId(principal.getName())
            );
            AttendanceResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(AttendanceResponse.from(result));
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
