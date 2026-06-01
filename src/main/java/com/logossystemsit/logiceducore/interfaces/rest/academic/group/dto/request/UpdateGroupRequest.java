package com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateGroupRequest(
        @JsonProperty("subjectId") String subjectId,
        @JsonProperty("academicPeriodId") String academicPeriodId,
        @JsonProperty("branchId") String branchId,
        @JsonProperty("teacherId") String teacherId,
        @JsonProperty("code") String code,
        @JsonProperty("capacity") int capacity
) {}
