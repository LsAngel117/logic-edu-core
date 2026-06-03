package com.logossystemsit.logiceducore.domain.academic.group.model.valueobject;

import java.util.UUID;

public record ScheduleId(String value) {

    public ScheduleId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ScheduleId is required");
        }
        value = value.trim();
    }

    public static ScheduleId generate() {
        return new ScheduleId(UUID.randomUUID().toString());
    }
}
