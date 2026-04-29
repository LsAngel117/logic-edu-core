package com.logossystemsit.logiceducore.application.membership.usecase;

import com.logossystemsit.logiceducore.application.membership.dto.MembershipResult;
import com.logossystemsit.logiceducore.application.membership.port.in.GetUserMembershipsUseCase;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.util.List;

public class GetUserMembershipsService implements GetUserMembershipsUseCase {

    private final MembershipRepository repository;

    public GetUserMembershipsService(MembershipRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MembershipResult> execute(UserId userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(MembershipResult::from)
                .toList();
    }
}
