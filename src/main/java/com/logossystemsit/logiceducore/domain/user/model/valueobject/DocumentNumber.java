package com.logossystemsit.logiceducore.domain.user.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

public class DocumentNumber {

    private final String value;

    public DocumentNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Document number is required");
        }

        String normalized = value.trim();

        if (!normalized.matches("^[0-9A-Za-z]+$")) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Invalid document format");
        }

        this.value = normalized;
    }

    public String getValue() {
        return value;
    }
}
