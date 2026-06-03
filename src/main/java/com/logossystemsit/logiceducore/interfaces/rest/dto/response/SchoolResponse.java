package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolResponse(
        @Schema(description = "Identificador único de la institución", example = "01JT5D4G5H6I7J8K9L0M1N2")
        String id,
        @Schema(description = "Nombre completo de la institución", example = "Colegio Nacional San José")
        String name,
        @Schema(description = "Código único de la institución", example = "COL-001")
        String code,
        @Schema(description = "Nombre corto o sigla", example = "CNSJ")
        String shortName,
        @Schema(description = "Descripción de la institución", example = "Institución de educación básica secundaria")
        String description,
        @Schema(description = "Correo electrónico institucional", example = "info@sanjose.edu.co")
        String email,
        @Schema(description = "Teléfono de contacto", example = "+57 601 2345678")
        String phone,
        @Schema(description = "Dirección física", example = "Calle 123 #45-67, Bogotá")
        String address,
        @Schema(description = "Estado actual: ACTIVE o INACTIVE", example = "ACTIVE")
        String status,
        @Schema(description = "Fecha de creación en formato ISO", example = "2025-06-03T12:00:00Z")
        String createdAt,
        @Schema(description = "Fecha de última actualización en formato ISO", example = "2025-06-03T12:00:00Z")
        String updatedAt
) {}
