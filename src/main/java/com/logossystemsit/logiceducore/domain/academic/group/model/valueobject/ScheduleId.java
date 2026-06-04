package com.logossystemsit.logiceducore.domain.academic.group.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record ScheduleId(String value) {

    public ScheduleId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "ScheduleId is required");
        }
        value = value.trim();
    }

    public static ScheduleId generate() {
        return new ScheduleId(UUID.randomUUID().toString());
    }
}
