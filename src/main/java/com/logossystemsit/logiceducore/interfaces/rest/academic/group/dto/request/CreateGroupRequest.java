package com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CreateGroupRequest(
        @JsonProperty("subjectId") String subjectId,
        @JsonProperty("academicPeriodId") String academicPeriodId,
        @JsonProperty("branchId") String branchId,
        @JsonProperty("teacherId") String teacherId,
        @JsonProperty("code") String code,
        @JsonProperty("capacity") int capacity,
        @JsonProperty("schedules") List<ScheduleRequest> schedules
) {

    public record ScheduleRequest(
            @JsonProperty("dayOfWeek") String dayOfWeek,
            @JsonProperty("startTime") String startTime,
            @JsonProperty("endTime") String endTime,
            @JsonProperty("classroom") String classroom
    ) {}
}
