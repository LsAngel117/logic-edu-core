package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

public record MembershipResponse(
        String id,
        String userId,
        String role,
        String scopeType,
        String scopeRefId,
        boolean active
) {
}
