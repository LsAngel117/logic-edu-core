package com.logossystemsit.logiceducore.interfaces.rest.branch.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record BranchResponse(
        @Schema(description = "Identificador único de la sede", example = "01JT5E5H6I7J8K9L0M1N2O3P")
        String id,
        @Schema(description = "Identificador de la institución a la que pertenece", example = "01JT5D4G5H6I7J8K9L0M1N2")
        String schoolId,
        @Schema(description = "Nombre de la sede", example = "Sede Principal Norte")
        String name,
        @Schema(description = "Código único de la sede", example = "SED-001")
        String code,
        @Schema(description = "Nombre corto de la sede", example = "NORTE")
        String shortName,
        @Schema(description = "Descripción de la sede", example = "Sede principal ubicada en la zona norte")
        String description,
        @Schema(description = "Correo electrónico de la sede", example = "sede.norte@sanjose.edu.co")
        String email,
        @Schema(description = "Teléfono de la sede", example = "+57 601 3456789")
        String phone,
        @Schema(description = "Dirección física de la sede", example = "Carrera 45 #67-89, Bogotá")
        String address,
        @Schema(description = "Ciudad", example = "Medellín")
        String city,
        @Schema(description = "País", example = "Colombia")
        String country,
        @Schema(description = "Tipo de sede: MAIN, SECONDARY o VIRTUAL", example = "MAIN")
        String type,
        @Schema(description = "Estado actual: ACTIVE o INACTIVE", example = "ACTIVE")
        String status,
        @Schema(description = "Fecha de creación en formato ISO", example = "2025-06-03T12:00:00Z")
        String createdAt,
        @Schema(description = "Fecha de última actualización en formato ISO", example = "2025-06-03T12:00:00Z")
        String updatedAt
) {}
