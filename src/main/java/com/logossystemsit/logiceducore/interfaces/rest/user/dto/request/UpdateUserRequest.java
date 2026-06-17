package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateUserRequest(
        @Schema(description = "Correo electrónico", example = "usuario@institucion.edu.co")
        String email,
        @Schema(description = "Primer nombre", example = "Juan")
        String firstGivenName,
        @Schema(description = "Segundo nombre", example = "Carlos")
        String secondGivenName,
        @Schema(description = "Primer apellido", example = "Pérez")
        String firstFamilyName,
        @Schema(description = "Segundo apellido", example = "Gómez")
        String secondFamilyName,
        @Schema(description = "Sexo", example = "MALE")
        String sex,
        @Schema(description = "Fecha de nacimiento", example = "2000-01-15")
        String birthDate,
        @Schema(description = "Tipo de documento", example = "CC")
        String documentType,
        @Schema(description = "Número de documento", example = "1234567890")
        String documentValue,
        @Schema(description = "Teléfono", example = "+57 300 123 4567")
        String phone,
        @Schema(description = "Dirección", example = "Calle 123 #45-67")
        String address,
        @Schema(description = "Ciudad", example = "Medellín")
        String city,
        @Schema(description = "País", example = "Colombia")
        String country
) {}
