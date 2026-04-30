package com.logossystemsit.logiceducore.domain.branch.model.valueobject;

import java.util.Objects;

public final class BranchName {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 120;

    private final String value;

    private BranchName(String value) {
        this.value = normalize(value);
    }

    public static BranchName of(String value) {
        return new BranchName(value);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BranchName is required");
        }

        String normalized = value.trim().replaceAll("\\s+", " ");

        if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "BranchName must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters"
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
        if (!(o instanceof BranchName branchName)) return false;
        return Objects.equals(value, branchName.value);
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
