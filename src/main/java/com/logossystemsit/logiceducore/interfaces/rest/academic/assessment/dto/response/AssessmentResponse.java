package com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.dto.response;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

public record AssessmentResponse(
        @Schema(description = "Identificador único de la evaluación", example = "01JT6N5R6S7T8U9V0W1X2Y3Z")
        String id,
        @Schema(description = "Identificador del grupo", example = "01JT6K1N2O3P4Q5R6S7T8U9V")
        String groupId,
        @Schema(description = "Identificador del corte evaluativo (puede ser nulo)", example = "01JT6I9L0M1N2O3P4Q5R6S7T")
        String evaluationPeriodId,
        @Schema(description = "Nombre de la evaluación", example = "Examen final de Matemáticas")
        String name,
        @Schema(description = "Tipo de evaluación", example = "EXAM")
        String type,
        @Schema(description = "Peso porcentual", example = "0.30")
        BigDecimal weight,
        @Schema(description = "Puntaje máximo", example = "100.00")
        BigDecimal maxScore,
        @Schema(description = "Fecha de creación", example = "2025-06-03T12:00:00Z")
        Instant createdAt,
        @Schema(description = "Fecha de última actualización", example = "2025-06-03T12:00:00Z")
        Instant updatedAt
) {
    public static AssessmentResponse from(AssessmentResult result) {
        return new AssessmentResponse(
                result.id(),
                result.groupId(),
                result.evaluationPeriodId(),
                result.name(),
                result.type(),
                result.weight(),
                result.maxScore(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
