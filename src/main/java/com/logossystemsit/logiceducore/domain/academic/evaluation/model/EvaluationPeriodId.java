package com.logossystemsit.logiceducore.domain.academic.evaluation.model;

import java.util.UUID;

public record EvaluationPeriodId(String value) {

    public EvaluationPeriodId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EvaluationPeriodId is required");
        }
        value = value.trim();
    }

    public static EvaluationPeriodId generate() {
        return new EvaluationPeriodId(UUID.randomUUID().toString());
    }
}
