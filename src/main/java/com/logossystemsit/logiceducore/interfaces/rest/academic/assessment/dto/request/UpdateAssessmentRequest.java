package com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record UpdateAssessmentRequest(
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("weight") BigDecimal weight,
        @JsonProperty("maxScore") BigDecimal maxScore,
        @JsonProperty("evaluationPeriodId") String evaluationPeriodId
) {}
