package com.logossystemsit.logiceducore.domain.user.model.valueobject;

import java.util.UUID;

public record UserId(String value) {
    public UserId(String value) {
        if (value == null || !value.matches("^[0-9a-f-]{36}$")) {
            throw new IllegalArgumentException("Invalid user id: " + value);
        }
        this.value = value.toLowerCase();  // normaliza a minúsculas
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId userId)) return false;
        return value.equals(userId.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
