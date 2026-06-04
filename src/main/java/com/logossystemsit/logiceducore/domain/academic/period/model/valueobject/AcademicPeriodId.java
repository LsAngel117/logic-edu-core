package com.logossystemsit.logiceducore.domain.academic.period.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record AcademicPeriodId(String value) {

    public AcademicPeriodId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "AcademicPeriodId is required");
        }
        value = value.trim();
    }

    public static AcademicPeriodId generate() {
        return new AcademicPeriodId(UUID.randomUUID().toString());
    }
}
