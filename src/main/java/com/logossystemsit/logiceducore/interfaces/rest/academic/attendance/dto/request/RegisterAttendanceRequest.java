package com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record RegisterAttendanceRequest(
        @JsonProperty("studentId") String studentId,
        @JsonProperty("date") LocalDate date,
        @JsonProperty("status") String status,
        @JsonProperty("observations") String observations
) {}
