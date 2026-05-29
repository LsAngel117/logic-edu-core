package com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateAcademicLevelRequest(
        @JsonProperty("name") String name,
        @JsonProperty("number") int number
) {}
