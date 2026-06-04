package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.port.in.ToggleMembershipUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ToggleMembershipService implements ToggleMembershipUseCase {

    private final MembershipRepository repository;

    public ToggleMembershipService(MembershipRepository repository) {
        this.repository = repository;
    }

    @Override
    public void activate(MembershipId id) {
        var m = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MEMBERSHIP_NOT_FOUND, "Membership not found"));
        repository.save(m.activate());
    }

    @Override
    public void deactivate(MembershipId id) {
        var m = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MEMBERSHIP_NOT_FOUND, "Membership not found"));

        // Guard: user must always have at least one active membership
        var activeCount = repository.findByUserId(m.getUserId())
                .stream()
                .filter(Membership::isActive)
                .count();

        if (activeCount <= 1) {
            throw new BusinessRuleException(ErrorCode.MEMBERSHIP_LAST_ACTIVE, "Cannot deactivate the last active membership");
        }

        repository.save(m.deactivate());
    }
}
