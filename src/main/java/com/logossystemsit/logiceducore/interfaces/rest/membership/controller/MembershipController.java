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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memberships")
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
    public ResponseEntity<List<MembershipResponse>> getUserMemberships(@PathVariable String userId) {
        UserId uid = new UserId(userId);
        List<MembershipResult> results = getUserMembershipsUseCase.execute(uid);

        List<MembershipResponse> responses = results.stream()
                .map(this::toMembershipResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        MembershipId membershipId = new MembershipId(id);
        toggleMembershipUseCase.deactivate(membershipId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<MembershipResponse> activate(@PathVariable String id) {
        MembershipId membershipId = new MembershipId(id);
        toggleMembershipUseCase.activate(membershipId);

        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));

        return ResponseEntity.ok(toMembershipResponse(membership));
    }

    @PatchMapping("/{id}/role")
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
