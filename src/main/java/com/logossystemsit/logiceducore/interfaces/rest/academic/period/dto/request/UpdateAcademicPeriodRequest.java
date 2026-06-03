package com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateAcademicPeriodRequest(
        @Schema(description = "Nombre descriptivo del período", example = "Semestre 1 - 2025")
        String name,
        @Schema(description = "Número de secuencia dentro del nivel", example = "1")
        String sequence,
        @Schema(description = "Tipo de período: SEMESTER, TRIMESTER, BIMESTER, CUSTOM", example = "SEMESTER")
        String periodType,
        @Schema(description = "Fecha de inicio en formato YYYY-MM-DD", example = "2025-01-15")
        String startDate,
        @Schema(description = "Fecha de fin en formato YYYY-MM-DD", example = "2025-06-15")
        String endDate
) {}
