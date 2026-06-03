package com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateAcademicLevelRequest(
        @JsonProperty("name")
        @Schema(description = "Nombre del nivel o año", example = "Primero")
        String name,
        @JsonProperty("number")
        @Schema(description = "Número ordinal del nivel", example = "1")
        int number
) {}
