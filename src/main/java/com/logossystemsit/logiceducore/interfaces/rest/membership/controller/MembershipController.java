package com.logossystemsit.logiceducore.interfaces.rest.membership.controller;

import com.logossystemsit.logiceducore.application.membership.dto.command.*;
import com.logossystemsit.logiceducore.application.membership.dto.result.MembershipResult;
import com.logossystemsit.logiceducore.application.membership.port.in.*;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.interfaces.rest.dto.response.MembershipResponse;
import com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request.*;

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
@RequestMapping("/api/v1/memberships")
@Tag(name = "Membresías", description = "Administración de roles y membresías de usuario")
public class MembershipController {

    private final AssignMembershipUseCase assignMembershipUseCase;
    private final GetUserMembershipsUseCase getUserMembershipsUseCase;
    private final ToggleMembershipUseCase toggleMembershipUseCase;
    private final ChangeMembershipRoleUseCase changeMembershipRoleUseCase;
    private final ChangeMembershipScopeUseCase changeMembershipScopeUseCase;
    private final MembershipRepository membershipRepository;

    public MembershipController(AssignMembershipUseCase assignMembershipUseCase,
                                GetUserMembershipsUseCase getUserMembershipsUseCase,
                                ToggleMembershipUseCase toggleMembershipUseCase,
                                ChangeMembershipRoleUseCase changeMembershipRoleUseCase,
                                ChangeMembershipScopeUseCase changeMembershipScopeUseCase,
                                MembershipRepository membershipRepository) {
        this.assignMembershipUseCase = assignMembershipUseCase;
        this.getUserMembershipsUseCase = getUserMembershipsUseCase;
        this.toggleMembershipUseCase = toggleMembershipUseCase;
        this.changeMembershipRoleUseCase = changeMembershipRoleUseCase;
        this.changeMembershipScopeUseCase = changeMembershipScopeUseCase;
        this.membershipRepository = membershipRepository;
    }

    @PostMapping
    @Operation(summary = "Asignar membresía", description = "Asigna un rol y alcance a un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<MembershipResponse> assign(@RequestBody AssignMembershipRequest request) {
        UserId userId = new UserId(request.userId());
        Role role = Role.valueOf(request.role().toUpperCase());
        Scope scope = Scope.from(Scope.Type.valueOf(request.scopeType().toUpperCase()), request.scopeRefId());

        AssignMembershipCommand command = new AssignMembershipCommand(userId, role, scope);
        assignMembershipUseCase.execute(command);

        List<Membership> memberships = membershipRepository.findByUserId(userId);
        Membership latest = memberships.getLast();

        return ResponseEntity.status(HttpStatus.CREATED).body(toMembershipResponse(latest));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Listar membresías", description = "Obtiene todas las membresías de un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<MembershipResponse>> getUserMemberships(@PathVariable String userId) {
        UserId uid = new UserId(userId);
        List<MembershipResult> results = getUserMembershipsUseCase.execute(uid);

        List<MembershipResponse> responses = results.stream()
                .map(this::toMembershipResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar membresía", description = "Desactiva una membresía existente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operación exitosa sin contenido"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        MembershipId membershipId = new MembershipId(id);
        toggleMembershipUseCase.deactivate(membershipId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activar membresía", description = "Reactiva una membresía desactivada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<MembershipResponse> activate(@PathVariable String id) {
        MembershipId membershipId = new MembershipId(id);
        toggleMembershipUseCase.activate(membershipId);

        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));

        return ResponseEntity.ok(toMembershipResponse(membership));
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Cambiar rol", description = "Modifica el rol de una membresía")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<MembershipResponse> changeRole(@PathVariable String id,
                                                          @RequestBody ChangeMembershipRoleRequest request) {
        MembershipId membershipId = new MembershipId(id);
        Role newRole = Role.valueOf(request.role().toUpperCase());

        ChangeMembershipRoleCommand command = new ChangeMembershipRoleCommand(membershipId, newRole);
        changeMembershipRoleUseCase.execute(command);

        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));

        return ResponseEntity.ok(toMembershipResponse(membership));
    }

    @PatchMapping("/{id}/scope")
    @Operation(summary = "Cambiar alcance", description = "Modifica el alcance de una membresía")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<MembershipResponse> changeScope(@PathVariable String id,
                                                           @RequestBody ChangeMembershipScopeRequest request) {
        MembershipId membershipId = new MembershipId(id);
        Scope newScope = Scope.from(Scope.Type.valueOf(request.scopeType().toUpperCase()), request.scopeRefId());

        ChangeMembershipScopeCommand command = new ChangeMembershipScopeCommand(membershipId, newScope);
        changeMembershipScopeUseCase.execute(command);

        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));

        return ResponseEntity.ok(toMembershipResponse(membership));
    }

    private MembershipResponse toMembershipResponse(Membership m) {
        return new MembershipResponse(
                m.getId().value(),
                m.getUserId().value(),
                m.getRole().name(),
                m.getScope().type().name(),
                m.getScope().referenceId().orElse(null),
                m.isActive()
        );
    }

    private MembershipResponse toMembershipResponse(MembershipResult r) {
        return new MembershipResponse(
                r.id(),
                r.userId(),
                r.role(),
                r.scopeType(),
                r.scopeRefId(),
                r.active()
        );
    }
}
