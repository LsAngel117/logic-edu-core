package com.logossystemsit.logiceducore.domain.school.model.valueobject;

import java.util.UUID;

public record SchoolId(String value) {

    public SchoolId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SchoolId is required");
        }
        value = value.trim();
    }

    public static SchoolId generate() {
        return new SchoolId(UUID.randomUUID().toString());
    }
}
