package com.logossystemsit.logiceducore.domain.user.model.valueobject;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;

public class PasswordHash {
    private final String value;

    public PasswordHash(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Password hash cannot be empty");
        }
        if (value.length() < 60) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Password hash must be at least 60 characters");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordHash that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
