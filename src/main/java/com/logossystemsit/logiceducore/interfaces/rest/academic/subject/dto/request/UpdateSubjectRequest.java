package com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateSubjectRequest(
        @JsonProperty("code") String code,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("hours") int hours
) {}
