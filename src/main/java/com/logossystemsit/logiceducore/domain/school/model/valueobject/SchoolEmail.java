package com.logossystemsit.logiceducore.domain.school.model.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class SchoolEmail {

    private static final int MAX_LENGTH = 254;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final String value;

    private SchoolEmail(String value) {
        this.value = normalize(value);
    }

    public static SchoolEmail of(String value) {
        return new SchoolEmail(value);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SchoolEmail is required");
        }

        String normalized = value.trim().toLowerCase();

        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("SchoolEmail must not exceed " + MAX_LENGTH + " characters");
        }

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid SchoolEmail format");
        }

        return normalized;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolEmail that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
