package com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record UpdateSchedulesRequest(
        @JsonProperty("schedules")
        @Schema(description = "Nueva lista de horarios del grupo")
        List<ScheduleItem> schedules
) {

    public record ScheduleItem(
            @JsonProperty("dayOfWeek")
            @Schema(description = "Día de la semana", example = "MONDAY")
            String dayOfWeek,
            @JsonProperty("startTime")
            @Schema(description = "Hora de inicio en formato HH:mm", example = "08:00")
            String startTime,
            @JsonProperty("endTime")
            @Schema(description = "Hora de fin en formato HH:mm", example = "10:00")
            String endTime,
            @JsonProperty("classroom")
            @Schema(description = "Aula o salón", example = "A-101")
            String classroom
    ) {}
}
