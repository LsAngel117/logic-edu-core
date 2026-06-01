package com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateAttendanceRequest(
        @JsonProperty("status") String status,
        @JsonProperty("observations") String observations
) {}
