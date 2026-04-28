package com.logossystemsit.logiceducore.domain.user.model.valueobject;

import java.util.Locale;
import java.util.Objects;

public class Email {

    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        if (!isValid(normalized)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        this.value = normalized;
    }

    private boolean isValid(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return value.equals(email.value);
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
