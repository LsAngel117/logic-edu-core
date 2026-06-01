package com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UpdateSchedulesRequest(
        @JsonProperty("schedules") List<ScheduleItem> schedules
) {

    public record ScheduleItem(
            @JsonProperty("dayOfWeek") String dayOfWeek,
            @JsonProperty("startTime") String startTime,
            @JsonProperty("endTime") String endTime,
            @JsonProperty("classroom") String classroom
    ) {}
}
