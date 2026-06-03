package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "Correo electrónico del usuario", example = "usuario@institucion.edu.co")
        String email,
        @Schema(description = "Contraseña del usuario", example = "MiPassword123")
        String rawPassword
) {
}
