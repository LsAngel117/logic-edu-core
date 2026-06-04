package com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record AssessmentId(String value) {

    public AssessmentId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "AssessmentId is required");
        }
        value = value.trim();
    }

    public static AssessmentId generate() {
        return new AssessmentId(UUID.randomUUID().toString());
    }
}
