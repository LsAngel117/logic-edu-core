package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
        @Schema(description = "Identificador único del usuario", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String id,
        @Schema(description = "Nombre de usuario único", example = "juan.perez")
        String username,
        @Schema(description = "Correo electrónico del usuario", example = "usuario@institucion.edu.co")
        String email,
        @Schema(description = "Nombre completo del usuario", example = "Juan Carlos Pérez Gómez")
        String fullName,
        @Schema(description = "Estado actual del usuario", example = "ACTIVE")
        String status,
        @Schema(description = "Sexo del usuario", example = "MALE")
        String sex,
        @Schema(description = "Fecha de nacimiento del usuario", example = "1998-04-19")
        String birthDate,
        @Schema(description = "Tipo de documento", example = "CC")
        String documentType,
        @Schema(description = "Número de documento", example = "1234567890")
        String documentValue,
        @Schema(description = "Fecha de creación", example = "2025-06-03T12:00:00Z")
        String createdAt,
        @Schema(description = "Teléfono", example = "+57 300 123 4567")
        String phone,
        @Schema(description = "Dirección", example = "Calle 123 #45-67")
        String address,
        @Schema(description = "Ciudad", example = "Medellín")
        String city,
        @Schema(description = "País", example = "Colombia")
        String country
) {}
