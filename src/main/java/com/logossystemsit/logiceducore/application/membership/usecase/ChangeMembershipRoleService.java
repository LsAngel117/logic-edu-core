package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.dto.command.ChangeMembershipRoleCommand;
import com.logossystemsit.logiceducore.application.membership.port.in.ChangeMembershipRoleUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;

public class ChangeMembershipRoleService implements ChangeMembershipRoleUseCase {

    private final MembershipRepository repository;

    public ChangeMembershipRoleService(MembershipRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void execute(ChangeMembershipRoleCommand command) {
        var membership = repository.findById(command.membershipId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MEMBERSHIP_NOT_FOUND, "Membership not found"));

        var updated = membership.changeRole(command.newRole());

        repository.save(updated);
    }
}
