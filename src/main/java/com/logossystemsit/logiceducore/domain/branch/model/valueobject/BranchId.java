package com.logossystemsit.logiceducore.domain.branch.model.valueobject;

import java.util.Objects;
import java.util.UUID;

public final class BranchId {

    private final String value;

    private BranchId(String value) {
        this.value = normalize(value);
    }

    public static BranchId of(String value) {
        return new BranchId(value);
    }

    public static BranchId generate() {
        return new BranchId(UUID.randomUUID().toString());
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BranchId is required");
        }
        return value.trim();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BranchId branchId)) return false;
        return Objects.equals(value, branchId.value);
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
