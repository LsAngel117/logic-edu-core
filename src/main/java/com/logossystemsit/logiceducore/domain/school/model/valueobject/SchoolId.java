package com.logossystemsit.logiceducore.domain.school.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record SchoolId(String value) {

    public SchoolId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "SchoolId is required");
        }
        value = value.trim();
    }

    public static SchoolId generate() {
        return new SchoolId(UUID.randomUUID().toString());
    }
}
