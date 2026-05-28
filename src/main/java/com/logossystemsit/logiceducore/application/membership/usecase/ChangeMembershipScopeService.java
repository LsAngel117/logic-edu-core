package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.dto.command.ChangeMembershipScopeCommand;
import com.logossystemsit.logiceducore.application.membership.port.in.ChangeMembershipScopeUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;

import org.springframework.transaction.annotation.Transactional;

public class ChangeMembershipScopeService implements ChangeMembershipScopeUseCase {

    private final MembershipRepository repository;

    public ChangeMembershipScopeService(MembershipRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void execute(ChangeMembershipScopeCommand command) {

        var membership = repository.findById(command.membershipId())
                .orElseThrow();

        var updated = membership.changeScope(command.newScope());

        repository.save(updated);
    }
}
