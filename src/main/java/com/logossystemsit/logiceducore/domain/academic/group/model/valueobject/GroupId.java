package com.logossystemsit.logiceducore.domain.academic.group.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record GroupId(String value) {

    public GroupId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "GroupId is required");
        }
        value = value.trim();
    }

    public static GroupId generate() {
        return new GroupId(UUID.randomUUID().toString());
    }
}
