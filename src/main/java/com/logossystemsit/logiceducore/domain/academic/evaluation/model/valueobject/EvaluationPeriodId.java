package com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record EvaluationPeriodId(String value) {

    public EvaluationPeriodId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "EvaluationPeriodId is required");
        }
        value = value.trim();
    }

    public static EvaluationPeriodId generate() {
        return new EvaluationPeriodId(UUID.randomUUID().toString());
    }
}
