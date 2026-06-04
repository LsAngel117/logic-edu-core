package com.logossystemsit.logiceducore.domain.academic.group.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.ScheduleId;

import java.time.LocalTime;
import java.util.Objects;

public record Schedule(
        ScheduleId scheduleId,
        String dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        String classroom
) {

    public Schedule {
        Objects.requireNonNull(scheduleId, "scheduleId is required");
        Objects.requireNonNull(dayOfWeek, "dayOfWeek is required");
        Objects.requireNonNull(startTime, "startTime is required");
        Objects.requireNonNull(endTime, "endTime is required");

        if (!startTime.isBefore(endTime)) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "startTime must be before endTime");
        }
    }
}
