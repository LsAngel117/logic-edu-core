package com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.response;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record EvaluationPeriodResponse(
        @Schema(description = "Identificador único del corte evaluativo", example = "01JT6I9L0M1N2O3P4Q5R6S7T")
        String id,
        @Schema(description = "Identificador del período al que pertenece", example = "01JT6H8K9L0M1N2O3P4Q5R6S")
        String periodId,
        @Schema(description = "Nombre del corte evaluativo", example = "Primer corte")
        String name,
        @Schema(description = "Número de secuencia", example = "1")
        int sequence,
        @Schema(description = "Peso porcentual del corte", example = "0.33")
        BigDecimal weight,
        @Schema(description = "Fecha de inicio en formato ISO", example = "2025-02-01")
        String startDate,
        @Schema(description = "Fecha de fin en formato ISO", example = "2025-03-15")
        String endDate,
        @Schema(description = "Estado: ACTIVE o INACTIVE", example = "ACTIVE")
        String status,
        @Schema(description = "Fecha de creación", example = "2025-06-03T12:00:00Z")
        Instant createdAt,
        @Schema(description = "Fecha de última actualización", example = "2025-06-03T12:00:00Z")
        Instant updatedAt
) {
    public static EvaluationPeriodResponse from(EvaluationPeriodResult result) {
        return new EvaluationPeriodResponse(
                result.id(),
                result.periodId(),
                result.name(),
                result.sequence(),
                result.weight(),
                result.startDate() != null ? result.startDate().toString() : null,
                result.endDate() != null ? result.endDate().toString() : null,
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
