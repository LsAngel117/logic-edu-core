package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.port.in.ToggleMembershipUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;

public class ToggleMembershipService implements ToggleMembershipUseCase {

    private final MembershipRepository repository;

    public ToggleMembershipService(MembershipRepository repository) {
        this.repository = repository;
    }

    @Override
    public void activate(MembershipId id) {
        var m = repository.findById(id).orElseThrow();
        repository.save(m.activate());
    }

    @Override
    public void deactivate(MembershipId id) {
        var m = repository.findById(id).orElseThrow();
        repository.save(m.deactivate());
    }
}
