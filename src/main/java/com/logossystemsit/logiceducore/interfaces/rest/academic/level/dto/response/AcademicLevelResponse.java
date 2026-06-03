package com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.response;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;

import io.swagger.v3.oas.annotations.media.Schema;

public record AcademicLevelResponse(
        @Schema(description = "Identificador único del nivel", example = "01JT6G7J8K9L0M1N2O3P4Q5R")
        String id,
        @Schema(description = "Identificador de la institución", example = "01JT5D4G5H6I7J8K9L0M1N2")
        String schoolId,
        @Schema(description = "Nombre del nivel académico", example = "Primero")
        String name,
        @Schema(description = "Número ordinal del nivel", example = "1")
        int number,
        @Schema(description = "Estado del nivel: ACTIVE o INACTIVE", example = "ACTIVE")
        String status,
        @Schema(description = "Fecha de creación en formato ISO", example = "2025-06-03T12:00:00Z")
        String createdAt,
        @Schema(description = "Fecha de última actualización en formato ISO", example = "2025-06-03T12:00:00Z")
        String updatedAt
) {
    public static AcademicLevelResponse from(AcademicLevelResult result) {
        return new AcademicLevelResponse(
                result.id(),
                result.schoolId(),
                result.name(),
                result.number(),
                result.status(),
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
