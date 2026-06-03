package com.logossystemsit.logiceducore.interfaces.rest.academic.enrollment.dto.response;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;

import io.swagger.v3.oas.annotations.media.Schema;

public record EnrollmentResponse(
        @Schema(description = "Identificador único de la matrícula", example = "01JT6L3P4Q5R6S7T8U9V0W1X")
        String id,
        @Schema(description = "Identificador del estudiante", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String userId,
        @Schema(description = "Identificador del grupo", example = "01JT6K1N2O3P4Q5R6S7T8U9V")
        String groupId,
        @Schema(description = "Estado de la matrícula: ENROLLED o DROPPED", example = "ENROLLED")
        String status,
        @Schema(description = "Fecha de inscripción en formato ISO", example = "2025-06-03T12:00:00Z")
        String enrolledAt,
        @Schema(description = "Fecha de última actualización en formato ISO", example = "2025-06-03T12:00:00Z")
        String updatedAt
) {
    public static EnrollmentResponse from(EnrollmentResult result) {
        return new EnrollmentResponse(
                result.id(),
                result.userId(),
                result.groupId(),
                result.status(),
                result.enrolledAt() != null ? result.enrolledAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
