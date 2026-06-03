package com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateSubjectRequest(
        @JsonProperty("code")
        @Schema(description = "Código único de la materia", example = "MAT-001")
        String code,
        @JsonProperty("name")
        @Schema(description = "Nombre de la materia", example = "Matemáticas")
        String name,
        @JsonProperty("description")
        @Schema(description = "Descripción de la materia (opcional)", example = "Matemáticas básicas de secundaria")
        String description,
        @JsonProperty("hours")
        @Schema(description = "Horas académicas por período", example = "4")
        int hours
) {}
