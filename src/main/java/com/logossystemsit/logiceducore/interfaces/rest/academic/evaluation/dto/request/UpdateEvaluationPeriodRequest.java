package com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateEvaluationPeriodRequest(
        @Schema(description = "Nombre del corte evaluativo", example = "Primer corte")
        String name,
        @Schema(description = "Número de secuencia dentro del período", example = "1")
        String sequence,
        @Schema(description = "Peso porcentual del corte (ej. 0.33 para 33%)", example = "0.33")
        String weight,
        @Schema(description = "Fecha de inicio en formato YYYY-MM-DD", example = "2025-02-01")
        String startDate,
        @Schema(description = "Fecha de fin en formato YYYY-MM-DD", example = "2025-03-15")
        String endDate
) {}
