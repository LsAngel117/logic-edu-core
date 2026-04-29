package com.logossystemsit.logiceducore.application.membership.dto;

import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public record AssignMembershipCommand(
        UserId userId,
        Role role,
        Scope scope
) {}
