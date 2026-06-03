package com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateAcademicStructureRequest(
        @Schema(description = "Tipo de estructura: PRIMARIA, SECUNDARIA, MEDIA, UNIVERSITARIA, PERSONALIZADA", example = "SECUNDARIA")
        String structureType,
        @Schema(description = "Cantidad de niveles o años", example = "6")
        int levelsCount,
        @Schema(description = "Períodos académicos por nivel", example = "2")
        int periodsPerLevel,
        @Schema(description = "Cortes evaluativos por período", example = "3")
        int evaluationPeriodsPerPeriod,
        @Schema(description = "Materias por período", example = "8")
        int subjectsPerPeriod,
        @Schema(description = "Horas por materia", example = "4")
        int hoursPerSubject
) {}
