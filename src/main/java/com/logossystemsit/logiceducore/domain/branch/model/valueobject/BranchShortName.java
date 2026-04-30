package com.logossystemsit.logiceducore.domain.branch.model.valueobject;

import java.util.Objects;

public final class BranchShortName {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 60;

    private final String value;

    private BranchShortName(String value) {
        this.value = normalize(value);
    }

    public static BranchShortName of(String value) {
        return new BranchShortName(value);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BranchShortName is required");
        }

        String normalized = value.trim().replaceAll("\\s+", " ");

        if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "BranchShortName must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters"
            );
        }
        return normalized;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BranchShortName that)) return false;
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
