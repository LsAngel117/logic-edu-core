package com.logossystemsit.logiceducore.application.membership.dto.result;

import com.logossystemsit.logiceducore.domain.membership.model.Membership;

public record MembershipResult(
        String id,
        String userId,
        String role,
        String scopeType,
        String scopeRefId,
        boolean active
) {

    public static MembershipResult from(Membership m) {
        return new MembershipResult(
                m.getId().value(),
                m.getUserId().value(),
                m.getRole().name(),
                m.getScope().type().name(),
                m.getScope().referenceId().orElse(null),
                m.isActive()
        );
    }
}
