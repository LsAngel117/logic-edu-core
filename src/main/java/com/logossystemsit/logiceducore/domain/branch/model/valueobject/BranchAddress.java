package com.logossystemsit.logiceducore.domain.branch.model.valueobject;

import java.util.Objects;
import java.util.Optional;

public final class BranchAddress {

    private static final int MAX_LENGTH = 255;

    private final String value;

    private BranchAddress(String value) {
        this.value = normalize(value);
    }

    public static BranchAddress of(String value) {
        return new BranchAddress(value);
    }

    public static BranchAddress empty() {
        return new BranchAddress(null);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().replaceAll("\\s+", " ");

        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "BranchAddress must not exceed " + MAX_LENGTH + " characters"
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
        if (!(o instanceof BranchAddress that)) return false;
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
