package com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeMembershipRoleRequest(
        @Schema(description = "Nuevo rol: PLATFORM_ADMIN, SCHOOL_ADMIN, TEACHER o STUDENT", example = "SCHOOL_ADMIN")
        String role
) {
}
