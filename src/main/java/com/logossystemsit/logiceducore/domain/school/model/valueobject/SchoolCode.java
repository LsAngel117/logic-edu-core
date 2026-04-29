package com.logossystemsit.logiceducore.domain.school.model.valueobject;

import java.util.Objects;

public final class SchoolCode {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;

    private static final String CODE_REGEX =
            "^[A-Z0-9\\-]+$";

    private final String value;

    public SchoolCode(String value) {
        Objects.requireNonNull(value, "School code is required");

        String normalized = normalize(value);

        validateLength(normalized);
        validateFormat(normalized);

        this.value = normalized;
    }

    private String normalize(String value) {
        return value.trim().toUpperCase();
    }

    private void validateLength(String value) {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("School code length must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH);
        }
    }

    private void validateFormat(String value) {
        if (!value.matches(CODE_REGEX)) {
            throw new IllegalArgumentException("Invalid school code format");
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolCode that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
