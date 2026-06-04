package com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record AttendanceId(String value) {

    public AttendanceId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "AttendanceId is required");
        }
        value = value.trim();
    }

    public static AttendanceId generate() {
        return new AttendanceId(UUID.randomUUID().toString());
    }
}
