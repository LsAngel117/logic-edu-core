package com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request;

public record AssignMembershipRequest(
        String userId,
        String role,
        String scopeType,
        String scopeRefId
) {
}
