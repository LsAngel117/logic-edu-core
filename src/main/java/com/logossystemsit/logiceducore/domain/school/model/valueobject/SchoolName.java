package com.logossystemsit.logiceducore.domain.school.model.valueobject;

import java.util.Objects;

public final class SchoolName {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 100;

    private static final String NAME_REGEX =
            "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .\\-]+$";

    private final String value;

    public SchoolName(String value) {
        Objects.requireNonNull(value, "School name is required");

        String normalized = normalize(value);

        validateLength(normalized);
        validateFormat(normalized);

        this.value = normalized;
    }

    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private void validateLength(String value) {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("School name length must be between "
                    + MIN_LENGTH + " and " + MAX_LENGTH);
        }
    }

    private void validateFormat(String value) {
        if (!value.matches(NAME_REGEX)) {
            throw new IllegalArgumentException("Invalid school name format");
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolName that)) return false;
        return value.equalsIgnoreCase(that.value);
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
