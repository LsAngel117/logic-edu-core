package com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.response;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

public record AcademicPeriodResponse(
        @Schema(description = "Identificador único del período", example = "01JT6H8K9L0M1N2O3P4Q5R6S")
        String id,
        @Schema(description = "Identificador del nivel al que pertenece", example = "01JT6G7J8K9L0M1N2O3P4Q5R")
        String levelId,
        @Schema(description = "Tipo de período", example = "SEMESTER")
        String periodType,
        @Schema(description = "Nombre descriptivo del período", example = "Semestre 1 - 2025")
        String name,
        @Schema(description = "Número de secuencia", example = "1")
        int sequence,
        @Schema(description = "Fecha de inicio en formato ISO", example = "2025-01-15")
        String startDate,
        @Schema(description = "Fecha de fin en formato ISO", example = "2025-06-15")
        String endDate,
        @Schema(description = "Estado del período: ACTIVE o INACTIVE", example = "ACTIVE")
        String status,
        @Schema(description = "Fecha de creación", example = "2025-06-03T12:00:00Z")
        Instant createdAt,
        @Schema(description = "Fecha de última actualización", example = "2025-06-03T12:00:00Z")
        Instant updatedAt
) {
    public static AcademicPeriodResponse from(AcademicPeriodResult result) {
        return new AcademicPeriodResponse(
                result.id(),
                result.levelId(),
                result.periodType(),
                result.name(),
                result.sequence(),
                result.startDate() != null ? result.startDate().toString() : null,
                result.endDate() != null ? result.endDate().toString() : null,
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
