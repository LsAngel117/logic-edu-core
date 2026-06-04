package com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

import java.util.UUID;

public record AcademicStructureId(String value) {

    public AcademicStructureId {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "AcademicStructureId is required");
        }
        value = value.trim();
    }

    public static AcademicStructureId generate() {
        return new AcademicStructureId(UUID.randomUUID().toString());
    }
}
