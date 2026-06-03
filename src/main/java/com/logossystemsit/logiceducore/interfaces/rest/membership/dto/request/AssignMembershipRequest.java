package com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AssignMembershipRequest(
        @Schema(description = "Identificador del usuario", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String userId,
        @Schema(description = "Rol a asignar: PLATFORM_ADMIN, SCHOOL_ADMIN, TEACHER, STUDENT", example = "TEACHER")
        String role,
        @Schema(description = "Tipo de alcance: SCHOOL, BRANCH, ALL", example = "SCHOOL")
        String scopeType,
        @Schema(description = "ID de referencia del alcance (schoolId o branchId)", example = "a1b2c3d4")
        String scopeRefId
) {
}
