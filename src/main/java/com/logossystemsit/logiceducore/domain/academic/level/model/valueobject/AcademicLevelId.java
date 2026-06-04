package com.logossystemsit.logiceducore.domain.academic.level.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record AcademicLevelId(String value) {

    public AcademicLevelId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "AcademicLevelId is required");
        }
        value = value.trim();
    }

    public static AcademicLevelId generate() {
        return new AcademicLevelId(UUID.randomUUID().toString());
    }
}
