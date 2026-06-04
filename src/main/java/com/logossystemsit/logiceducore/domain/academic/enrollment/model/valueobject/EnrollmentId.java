package com.logossystemsit.logiceducore.domain.academic.enrollment.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record EnrollmentId(String value) {

    public EnrollmentId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "EnrollmentId is required");
        }
        value = value.trim();
    }

    public static EnrollmentId generate() {
        return new EnrollmentId(UUID.randomUUID().toString());
    }
}
