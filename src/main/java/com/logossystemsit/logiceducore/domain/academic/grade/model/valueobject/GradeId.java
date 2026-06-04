package com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record GradeId(String value) {

    public GradeId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "GradeId is required");
        }
        value = value.trim();
    }

    public static GradeId generate() {
        return new GradeId(UUID.randomUUID().toString());
    }
}
