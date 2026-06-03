package com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateAttendanceRequest(
        @JsonProperty("status")
        @Schema(description = "Nuevo estado de asistencia: PRESENT, ABSENT, LATE, EXCUSED", example = "LATE")
        String status,
        @JsonProperty("observations")
        @Schema(description = "Observaciones o justificación (opcional)", example = "Llegó 15 minutos tarde por transporte")
        String observations
) {}
