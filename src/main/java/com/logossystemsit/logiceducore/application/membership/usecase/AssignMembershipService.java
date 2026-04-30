package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.dto.command.AssignMembershipCommand;
import com.logossystemsit.logiceducore.application.membership.port.in.AssignMembershipUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;

public class AssignMembershipService implements AssignMembershipUseCase {

    private final MembershipRepository repository;

    public AssignMembershipService(MembershipRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(AssignMembershipCommand command) {

        var membership = Membership.create(
                command.userId(),
                command.role(),
                command.scope()
        );

        repository.save(membership);
    }
}
