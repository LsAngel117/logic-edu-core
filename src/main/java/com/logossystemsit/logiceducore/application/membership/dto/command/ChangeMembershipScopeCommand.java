package com.logossystemsit.logiceducore.application.membership.dto.command;

import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;

public record ChangeMembershipScopeCommand(
        MembershipId membershipId,
        Scope newScope
) {}
