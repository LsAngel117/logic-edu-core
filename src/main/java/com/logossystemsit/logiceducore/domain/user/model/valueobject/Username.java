package com.logossystemsit.logiceducore.domain.user.model.valueobject;

import java.util.Locale;
import java.util.Objects;

public class Username {

    private static final String USERNAME_REGEX = "^[a-z][a-z0-9]{2,20}$";

    private final String value;

    public Username(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        String normalized = normalize(value);

        if (!normalized.matches(USERNAME_REGEX)) {
            throw new IllegalArgumentException("Invalid username format");
        }

        this.value = normalized;
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Username)) return false;
        Username that = (Username) o;
        return value.equals(that.value);
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