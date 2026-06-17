package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
        @Schema(description = "Identificador único del usuario", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String id,
        @Schema(description = "Nombre de usuario único", example = "juan.perez")
        String username,
        @Schema(description = "Correo electrónico del usuario", example = "usuario@institucion.edu.co")
        String email,
        @Schema(description = "Nombre completo del usuario", example = "Juan Carlos Pérez")
        String fullName,
        @Schema(description = "Estado actual del usuario", example = "ACTIVE")
        String status,
        @Schema(description = "Fecha de creación en formato ISO", example = "2025-06-03")
        String createdAt,
        @Schema(description = "Teléfono", example = "+57 300 123 4567")
        String phone,
        @Schema(description = "Dirección", example = "Calle 123 #45-67")
        String address,
        @Schema(description = "Ciudad", example = "Medellín")
        String city,
        @Schema(description = "País", example = "Colombia")
        String country
) {
}
