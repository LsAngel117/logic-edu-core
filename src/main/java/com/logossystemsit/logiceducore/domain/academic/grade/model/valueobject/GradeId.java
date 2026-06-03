package com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject;

import java.util.UUID;

public record GradeId(String value) {

    public GradeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("GradeId is required");
        }
        value = value.trim();
    }

    public static GradeId generate() {
        return new GradeId(UUID.randomUUID().toString());
    }
}
