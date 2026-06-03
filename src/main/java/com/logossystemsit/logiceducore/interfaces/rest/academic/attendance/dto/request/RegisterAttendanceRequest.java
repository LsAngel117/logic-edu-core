package com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record RegisterAttendanceRequest(
        @JsonProperty("studentId")
        @Schema(description = "Identificador del estudiante", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String studentId,
        @JsonProperty("date")
        @Schema(description = "Fecha de la sesión en formato YYYY-MM-DD", example = "2025-06-03")
        LocalDate date,
        @JsonProperty("status")
        @Schema(description = "Estado de asistencia: PRESENT, ABSENT, LATE, EXCUSED", example = "PRESENT")
        String status,
        @JsonProperty("observations")
        @Schema(description = "Observaciones o justificación (opcional)", example = "Llegó 15 minutos tarde por transporte")
        String observations
) {}
