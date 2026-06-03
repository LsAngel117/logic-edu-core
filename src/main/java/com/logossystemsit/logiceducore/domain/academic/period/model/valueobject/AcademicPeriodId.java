package com.logossystemsit.logiceducore.domain.academic.period.model.valueobject;

import java.util.UUID;

public record AcademicPeriodId(String value) {

    public AcademicPeriodId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AcademicPeriodId is required");
        }
        value = value.trim();
    }

    public static AcademicPeriodId generate() {
        return new AcademicPeriodId(UUID.randomUUID().toString());
    }
}
