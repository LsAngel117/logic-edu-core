package com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeMembershipScopeRequest(
        @Schema(description = "Nuevo tipo de alcance: SCHOOL, BRANCH, ALL", example = "BRANCH")
        String scopeType,
        @Schema(description = "ID de referencia del nuevo alcance", example = "a1b2c3d4")
        String scopeRefId
) {
}
