package com.logossystemsit.logiceducore.domain.branch.model.valueobject;

import java.util.Objects;

public final class BranchPhone {

    private static final int MIN_DIGITS = 7;
    private static final int MAX_DIGITS = 15;

    private final String value;

    private BranchPhone(String value) {
        this.value = normalize(value);
    }

    public static BranchPhone of(String value) {
        return new BranchPhone(value);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BranchPhone is required");
        }

        String normalized = value.trim().replaceAll("[\\s\\-()]", "");

        if (!normalized.matches("^\\+?\\d+$")) {
            throw new IllegalArgumentException("BranchPhone must contain only digits and an optional leading '+'");
        }

        String digitsOnly = normalized.startsWith("+") ? normalized.substring(1) : normalized;

        if (digitsOnly.length() < MIN_DIGITS || digitsOnly.length() > MAX_DIGITS) {
            throw new IllegalArgumentException(
                    "BranchPhone must have between " + MIN_DIGITS + " and " + MAX_DIGITS + " digits"
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
        if (!(o instanceof BranchPhone branchPhone)) return false;
        return Objects.equals(value, branchPhone.value);
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
