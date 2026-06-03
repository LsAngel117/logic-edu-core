package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "Token JWT de autenticación", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,
        @Schema(description = "Identificador único del usuario", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String userId,
        @Schema(description = "Nombre de usuario", example = "juan.perez")
        String username
) {
}
