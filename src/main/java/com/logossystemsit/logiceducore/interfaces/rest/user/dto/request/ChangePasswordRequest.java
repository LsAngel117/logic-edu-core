package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangePasswordRequest(
        @Schema(description = "Contraseña actual del usuario", example = "MiPassword123")
        String currentPassword,
        @Schema(description = "Nueva contraseña", example = "MiNuevaPassword456")
        String newPassword
) {
}
