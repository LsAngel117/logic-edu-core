package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeUserStatusRequest(
        @Schema(description = "Nuevo estado: ACTIVE, INACTIVE o BLOCKED", example = "ACTIVE")
        String status
) {
}
