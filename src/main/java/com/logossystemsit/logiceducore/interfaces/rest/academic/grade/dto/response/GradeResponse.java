package com.logossystemsit.logiceducore.interfaces.rest.academic.grade.dto.response;

import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

public record GradeResponse(
        @Schema(description = "Identificador único de la calificación", example = "01JT6O6S7T8U9V0W1X2Y3Z4A")
        String id,
        @Schema(description = "Identificador de la evaluación", example = "01JT6N5R6S7T8U9V0W1X2Y3Z")
        String assessmentId,
        @Schema(description = "Identificador del estudiante", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String studentId,
        @Schema(description = "Valor de la nota", example = "85.50")
        BigDecimal value,
        @Schema(description = "Fecha en la que se registró la calificación", example = "2025-06-03T12:00:00Z")
        Instant gradedAt,
        @Schema(description = "Fecha de última actualización", example = "2025-06-03T12:00:00Z")
        Instant updatedAt
) {
    public static GradeResponse from(GradeResult result) {
        return new GradeResponse(
                result.id(),
                result.assessmentId(),
                result.studentId(),
                result.value(),
                result.gradedAt(),
                result.updatedAt()
        );
    }
}
