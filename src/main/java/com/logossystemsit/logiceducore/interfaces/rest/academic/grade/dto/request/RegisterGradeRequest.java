package com.logossystemsit.logiceducore.interfaces.rest.academic.grade.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record RegisterGradeRequest(
        @Schema(description = "Identificador del estudiante", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String studentId,
        @Schema(description = "Valor de la nota", example = "85.50")
        BigDecimal value
) {}
