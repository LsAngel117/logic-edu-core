package com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record UpdateAssessmentRequest(
        @JsonProperty("name")
        @Schema(description = "Nombre de la evaluación", example = "Examen final de Matemáticas")
        String name,
        @JsonProperty("type")
        @Schema(description = "Tipo de evaluación: EXAM, QUIZ, HOMEWORK, PROJECT, PARTICIPATION", example = "EXAM")
        String type,
        @JsonProperty("weight")
        @Schema(description = "Peso porcentual de la evaluación", example = "0.30")
        BigDecimal weight,
        @JsonProperty("maxScore")
        @Schema(description = "Puntaje máximo posible", example = "100.00")
        BigDecimal maxScore,
        @JsonProperty("evaluationPeriodId")
        @Schema(description = "Identificador del corte evaluativo al que pertenece (opcional)", example = "01JT6I9L0M1N2O3P4Q5R6S7T")
        String evaluationPeriodId
) {}
