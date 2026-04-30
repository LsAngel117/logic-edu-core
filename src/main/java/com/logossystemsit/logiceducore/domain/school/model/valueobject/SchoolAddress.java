package com.logossystemsit.logiceducore.domain.school.model.valueobject;

import java.util.Objects;
import java.util.Optional;

public final class SchoolAddress {

    private static final int MAX_LENGTH = 255;

    private final String value;

    private SchoolAddress(String value) {
        this.value = normalize(value);
    }

    public static SchoolAddress of(String value) {
        return new SchoolAddress(value);
    }

    public static SchoolAddress empty() {
        return new SchoolAddress(null);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().replaceAll("\\s+", " ");

        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "SchoolAddress must not exceed " + MAX_LENGTH + " characters"
            );
        }

        return normalized;
    }

    public Optional<String> value() {
        return Optional.ofNullable(value);
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolAddress that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value == null ? "" : value;
    }
}
