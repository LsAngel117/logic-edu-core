package com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject;

import java.util.UUID;

public record SubjectId(String value) {

    public SubjectId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SubjectId is required");
        }
        value = value.trim();
    }

    public static SubjectId generate() {
        return new SubjectId(UUID.randomUUID().toString());
    }
}
