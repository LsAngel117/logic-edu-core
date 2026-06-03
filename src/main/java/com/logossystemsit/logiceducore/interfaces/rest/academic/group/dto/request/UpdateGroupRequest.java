package com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateGroupRequest(
        @JsonProperty("subjectId")
        @Schema(description = "Identificador de la materia", example = "01JT6J0M1N2O3P4Q5R6S7T8U")
        String subjectId,
        @JsonProperty("academicPeriodId")
        @Schema(description = "Identificador del período académico", example = "01JT6H8K9L0M1N2O3P4Q5R6S")
        String academicPeriodId,
        @JsonProperty("branchId")
        @Schema(description = "Identificador de la sede", example = "01JT5E5H6I7J8K9L0M1N2O3P")
        String branchId,
        @JsonProperty("teacherId")
        @Schema(description = "Identificador del docente", example = "01JT5B2X3Y4Z5W6V7U8A9B0C")
        String teacherId,
        @JsonProperty("code")
        @Schema(description = "Código único del grupo", example = "GRP-MAT-001")
        String code,
        @JsonProperty("capacity")
        @Schema(description = "Capacidad máxima de estudiantes", example = "30")
        int capacity
) {}
