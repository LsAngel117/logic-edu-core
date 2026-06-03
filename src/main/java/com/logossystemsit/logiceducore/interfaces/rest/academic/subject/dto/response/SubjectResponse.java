package com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.response;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;

import io.swagger.v3.oas.annotations.media.Schema;

public record SubjectResponse(
        @Schema(description = "Identificador único de la materia", example = "01JT6J0M1N2O3P4Q5R6S7T8U")
        String id,
        @Schema(description = "Identificador de la institución", example = "01JT5D4G5H6I7J8K9L0M1N2")
        String schoolId,
        @Schema(description = "Código único de la materia", example = "MAT-001")
        String code,
        @Schema(description = "Nombre de la materia", example = "Matemáticas")
        String name,
        @Schema(description = "Descripción de la materia", example = "Matemáticas básicas de secundaria")
        String description,
        @Schema(description = "Horas académicas por período", example = "4")
        int hours,
        @Schema(description = "Estado de la materia: ACTIVE o INACTIVE", example = "ACTIVE")
        String status,
        @Schema(description = "Fecha de creación en formato ISO", example = "2025-06-03T12:00:00Z")
        String createdAt,
        @Schema(description = "Fecha de última actualización en formato ISO", example = "2025-06-03T12:00:00Z")
        String updatedAt
) {
    public static SubjectResponse from(SubjectResult result) {
        return new SubjectResponse(
                result.id(),
                result.schoolId(),
                result.code(),
                result.name(),
                result.description(),
                result.hours(),
                result.status(),
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
