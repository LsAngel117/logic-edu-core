package com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record SubjectId(String value) {

    public SubjectId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "SubjectId is required");
        }
        value = value.trim();
    }

    public static SubjectId generate() {
        return new SubjectId(UUID.randomUUID().toString());
    }
}
