package com.logossystemsit.logiceducore.domain.academic.level.model.valueobject;

import java.util.UUID;

public record AcademicLevelId(String value) {

    public AcademicLevelId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AcademicLevelId is required");
        }
        value = value.trim();
    }

    public static AcademicLevelId generate() {
        return new AcademicLevelId(UUID.randomUUID().toString());
    }
}
