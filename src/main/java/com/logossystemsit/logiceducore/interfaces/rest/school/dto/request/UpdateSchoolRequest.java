package com.logossystemsit.logiceducore.interfaces.rest.school.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateSchoolRequest(
        @Schema(description = "Nombre completo de la institución", example = "Colegio Nacional San José")
        String name,
        @Schema(description = "Código único de la institución", example = "COL-001")
        String code,
        @Schema(description = "Nombre corto o sigla", example = "CNSJ")
        String shortName,
        @Schema(description = "Descripción de la institución (opcional)", example = "Institución de educación básica secundaria")
        String description,
        @Schema(description = "Correo electrónico institucional (opcional)", example = "info@sanjose.edu.co")
        String email,
        @Schema(description = "Teléfono de contacto (opcional)", example = "+57 601 2345678")
        String phone,
        @Schema(description = "Dirección física (opcional)", example = "Calle 123 #45-67, Bogotá")
        String address,
        @Schema(description = "Ciudad", example = "Medellín")
        String city,
        @Schema(description = "País", example = "Colombia")
        String country
) {}
