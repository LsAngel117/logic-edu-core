package com.logossystemsit.logiceducore.domain.academic.assessment.model;

import java.util.UUID;

public record AssessmentId(String value) {

    public AssessmentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AssessmentId is required");
        }
        value = value.trim();
    }

    public static AssessmentId generate() {
        return new AssessmentId(UUID.randomUUID().toString());
    }
}
