package com.logossystemsit.logiceducore.interfaces.rest.academic.group.controller;

import com.logossystemsit.logiceducore.application.academic.group.dto.command.CreateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.command.ScheduleData;
import com.logossystemsit.logiceducore.application.academic.group.dto.command.UpdateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.*;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request.CreateGroupRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request.UpdateGroupRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request.UpdateSchedulesRequest;
import com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.response.GroupResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schools/{schoolId}/groups")
@Tag(name = "Grupos", description = "Oferta académica: grupos, horarios y capacidad")
public class GroupController {

    private final CreateGroupUseCase createUseCase;
    private final GetGroupUseCase getUseCase;
    private final ListGroupsBySchoolUseCase listUseCase;
    private final UpdateGroupUseCase updateUseCase;
    private final UpdateGroupSchedulesUseCase updateSchedulesUseCase;
    private final DeactivateGroupUseCase deactivateUseCase;

    public GroupController(
            CreateGroupUseCase createUseCase,
            GetGroupUseCase getUseCase,
            ListGroupsBySchoolUseCase listUseCase,
            UpdateGroupUseCase updateUseCase,
            UpdateGroupSchedulesUseCase updateSchedulesUseCase,
            DeactivateGroupUseCase deactivateUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
        this.updateSchedulesUseCase = updateSchedulesUseCase;
        this.deactivateUseCase = deactivateUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Crear grupo", description = "Oferta un grupo para una materia en un período y sede")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GroupResponse> create(
            @PathVariable String schoolId,
            @RequestBody CreateGroupRequest request) {
        try {
            CreateGroupCommand command = mapToCreateCommand(schoolId, request);
            GroupResult result = createUseCase.execute(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GroupResponse.from(result));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener grupo", description = "Consulta un grupo con sus horarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GroupResponse> getById(@PathVariable String id) {
        try {
            GroupId groupId = new GroupId(id);
            GroupResult result = getUseCase.execute(groupId);
            return ResponseEntity.ok(GroupResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar grupos", description = "Obtiene los grupos de una institución, con filtros opcionales por sede y período")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<GroupResponse>> listBySchool(
            @PathVariable String schoolId,
            @RequestParam(required = false) String branchId,
            @RequestParam(required = false) String periodId) {
        SchoolId sid = new SchoolId(schoolId);
        BranchId bid = branchId != null ? BranchId.of(branchId) : null;
        AcademicPeriodId pid = periodId != null ? new AcademicPeriodId(periodId) : null;

        List<GroupResult> results = listUseCase.execute(sid, bid, pid);
        List<GroupResponse> responses = results.stream()
                .map(GroupResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Actualizar grupo", description = "Modifica los datos de un grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GroupResponse> update(
            @PathVariable String schoolId,
            @PathVariable String id,
            @RequestBody UpdateGroupRequest request) {
        try {
            UpdateGroupCommand command = mapToUpdateCommand(schoolId, id, request);
            GroupResult result = updateUseCase.execute(command);
            return ResponseEntity.ok(GroupResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @PutMapping("/{id}/schedules")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Actualizar horarios", description = "Modifica los horarios de un grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GroupResponse> updateSchedules(
            @PathVariable String schoolId,
            @PathVariable String id,
            @RequestBody UpdateSchedulesRequest request) {
        try {
            GroupId groupId = new GroupId(id);
            List<ScheduleData> schedules = request.schedules().stream()
                    .map(s -> new ScheduleData(
                            s.dayOfWeek(),
                            LocalTime.parse(s.startTime()),
                            LocalTime.parse(s.endTime()),
                            s.classroom()))
                    .toList();
            GroupResult result = updateSchedulesUseCase.execute(groupId, schedules);
            return ResponseEntity.ok(GroupResponse.from(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','SCHOOL_ADMIN')")
    @Operation(summary = "Desactivar grupo", description = "Desactiva un grupo. Las matrículas existentes se conservan como histórico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GroupResponse> deactivate(@PathVariable String id) {
        try {
            GroupId groupId = new GroupId(id);
            GroupResult result = deactivateUseCase.execute(groupId);
            return ResponseEntity.ok(GroupResponse.from(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private CreateGroupCommand mapToCreateCommand(String schoolId, CreateGroupRequest r) {
        List<ScheduleData> schedules = r.schedules() != null ? r.schedules().stream()
                .map(s -> new ScheduleData(
                        s.dayOfWeek(),
                        LocalTime.parse(s.startTime()),
                        LocalTime.parse(s.endTime()),
                        s.classroom()))
                .toList() : List.of();

        return new CreateGroupCommand(
                new SchoolId(schoolId),
                new SubjectId(r.subjectId()),
                new AcademicPeriodId(r.academicPeriodId()),
                BranchId.of(r.branchId()),
                new UserId(r.teacherId()),
                r.code(),
                r.capacity(),
                schedules
        );
    }

    private UpdateGroupCommand mapToUpdateCommand(String schoolId, String id, UpdateGroupRequest r) {
        return new UpdateGroupCommand(
                new GroupId(id),
                new SchoolId(schoolId),
                new SubjectId(r.subjectId()),
                new AcademicPeriodId(r.academicPeriodId()),
                BranchId.of(r.branchId()),
                new UserId(r.teacherId()),
                r.code(),
                r.capacity()
        );
    }
}
