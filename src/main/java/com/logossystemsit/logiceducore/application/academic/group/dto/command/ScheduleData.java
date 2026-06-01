package com.logossystemsit.logiceducore.application.academic.group.dto.command;

import java.time.LocalTime;

public record ScheduleData(
        String dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        String classroom
) {}
