package com.logossystemsit.logiceducore.interfaces.rest.academic.grade.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record UpdateGradeRequest(
        @Schema(description = "Nuevo valor de la nota", example = "90.00")
        BigDecimal value
) {}
