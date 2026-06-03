package com.logossystemsit.logiceducore.domain.academic.enrollment.model.valueobject;

import java.util.UUID;

public record EnrollmentId(String value) {

    public EnrollmentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EnrollmentId is required");
        }
        value = value.trim();
    }

    public static EnrollmentId generate() {
        return new EnrollmentId(UUID.randomUUID().toString());
    }
}
