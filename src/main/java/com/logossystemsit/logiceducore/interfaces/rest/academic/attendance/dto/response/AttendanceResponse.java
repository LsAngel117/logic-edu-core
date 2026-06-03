package com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.response;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

public record AttendanceResponse(
        @Schema(description = "Identificador único del registro de asistencia", example = "01JT6M4Q5R6S7T8U9V0W1X2Y")
        String id,
        @Schema(description = "Identificador del grupo", example = "01JT6K1N2O3P4Q5R6S7T8U9V")
        String groupId,
        @Schema(description = "Identificador del estudiante", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String studentId,
        @Schema(description = "Fecha de la sesión", example = "2025-06-03")
        LocalDate date,
        @Schema(description = "Estado de asistencia: PRESENT, ABSENT, LATE, EXCUSED", example = "PRESENT")
        String status,
        @Schema(description = "Observaciones sobre la asistencia", example = "Llegó 15 minutos tarde por transporte")
        String observations,
        @Schema(description = "Fecha de creación del registro", example = "2025-06-03T12:00:00Z")
        Instant createdAt,
        @Schema(description = "Fecha de última actualización", example = "2025-06-03T12:00:00Z")
        Instant updatedAt
) {
    public static AttendanceResponse from(AttendanceResult result) {
        return new AttendanceResponse(
                result.id(),
                result.groupId(),
                result.studentId(),
                result.date(),
                result.status(),
                result.observations(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
