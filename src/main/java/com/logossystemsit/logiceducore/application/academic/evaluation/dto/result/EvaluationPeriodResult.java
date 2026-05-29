package com.logossystemsit.logiceducore.application.academic.evaluation.dto.result;

import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record EvaluationPeriodResult(
        String id,
        String periodId,
        String name,
        int sequence,
        BigDecimal weight,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static EvaluationPeriodResult from(EvaluationPeriod period) {
        return new EvaluationPeriodResult(
                period.getId().value(),
                period.getPeriodId().value(),
                period.getName(),
                period.getSequence(),
                period.getWeight(),
                period.getStartDate(),
                period.getEndDate(),
                period.getStatus().name(),
                period.getCreatedAt(),
                period.getUpdatedAt()
        );
    }
}
