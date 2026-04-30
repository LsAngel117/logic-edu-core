package com.logossystemsit.logiceducore.application.membership.dto.command;

import com.logossystemsit.logiceducore.domain.membership.model.valueobject.MembershipId;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;

public record ChangeMembershipRoleCommand(
        MembershipId membershipId,
        Role newRole
) {}
