package com.logossystemsit.logiceducore.domain.branch.model.valueobject;

import java.util.Objects;

public final class BranchCode {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 30;

    private final String value;

    private BranchCode(String value) {
        this.value = normalize(value);
    }

    public static BranchCode of(String value) {
        return new BranchCode(value);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BranchCode is required");
        }

        String normalized = value.trim().toUpperCase().replaceAll("\\s+", "");

        if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "BranchCode must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters"
            );
        }

        if (!normalized.matches("^[A-Z0-9_-]+$")) {
            throw new IllegalArgumentException(
                    "BranchCode can only contain uppercase letters, numbers, underscore and hyphen"
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
        if (!(o instanceof BranchCode branchCode)) return false;
        return Objects.equals(value, branchCode.value);
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
