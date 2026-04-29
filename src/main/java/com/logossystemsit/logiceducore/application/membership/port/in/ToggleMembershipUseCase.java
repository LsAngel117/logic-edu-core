package com.logossystemsit.logiceducore.application.membership.port.in;

import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;

public interface ToggleMembershipUseCase {
    void activate(MembershipId id);
    void deactivate(MembershipId id);
}
