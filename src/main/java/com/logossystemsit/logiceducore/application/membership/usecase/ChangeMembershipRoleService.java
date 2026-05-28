package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.dto.command.ChangeMembershipRoleCommand;
import com.logossystemsit.logiceducore.application.membership.port.in.ChangeMembershipRoleUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;

public class ChangeMembershipRoleService implements ChangeMembershipRoleUseCase {

    private final MembershipRepository repository;

    public ChangeMembershipRoleService(MembershipRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(ChangeMembershipRoleCommand command) {
        var membership = repository.findById(command.membershipId())
                .orElseThrow(() -> new IllegalArgumentException("Membership not found"));

        var updated = membership.changeRole(command.newRole());

        repository.save(updated);
    }
}
