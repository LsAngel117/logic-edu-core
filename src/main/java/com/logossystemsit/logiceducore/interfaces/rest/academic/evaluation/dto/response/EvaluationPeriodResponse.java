package com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.response;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record EvaluationPeriodResponse(
        String id,
        String periodId,
        String name,
        int sequence,
        BigDecimal weight,
        String startDate,
        String endDate,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static EvaluationPeriodResponse from(EvaluationPeriodResult result) {
        return new EvaluationPeriodResponse(
                result.id(),
                result.periodId(),
                result.name(),
                result.sequence(),
                result.weight(),
                result.startDate() != null ? result.startDate().toString() : null,
                result.endDate() != null ? result.endDate().toString() : null,
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
