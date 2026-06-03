package com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.response;

import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record AcademicStructureResponse(
        @Schema(description = "Identificador único de la estructura", example = "01JT6F6I7J8K9L0M1N2O3P4Q")
        String id,
        @Schema(description = "Identificador de la institución", example = "01JT5D4G5H6I7J8K9L0M1N2")
        String schoolId,
        @Schema(description = "Tipo de estructura académica", example = "SECUNDARIA")
        String structureType,
        @Schema(description = "Cantidad de niveles", example = "6")
        int levelsCount,
        @Schema(description = "Períodos por nivel", example = "2")
        int periodsPerLevel,
        @Schema(description = "Cortes evaluativos por período", example = "3")
        int evaluationPeriodsPerPeriod,
        @Schema(description = "Materias por período", example = "8")
        int subjectsPerPeriod,
        @Schema(description = "Horas por materia", example = "4")
        int hoursPerSubject,
        @Schema(description = "Indica si la estructura está activa", example = "true")
        boolean active,
        @Schema(description = "Versión de la estructura para control de concurrencia", example = "1")
        int version,
        @Schema(description = "Fecha de creación en formato ISO", example = "2025-06-03T12:00:00Z")
        String createdAt,
        @Schema(description = "Fecha de última actualización en formato ISO", example = "2025-06-03T12:00:00Z")
        String updatedAt
) {
    public static AcademicStructureResponse from(AcademicStructureResult result) {
        return new AcademicStructureResponse(
                result.id(),
                result.schoolId(),
                result.structureType(),
                result.levelsCount(),
                result.periodsPerLevel(),
                result.evaluationPeriodsPerPeriod(),
                result.subjectsPerPeriod(),
                result.hoursPerSubject(),
                result.active(),
                result.version(),
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
