package com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject;

import java.util.UUID;

public record AttendanceId(String value) {

    public AttendanceId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AttendanceId is required");
        }
        value = value.trim();
    }

    public static AttendanceId generate() {
        return new AttendanceId(UUID.randomUUID().toString());
    }
}
