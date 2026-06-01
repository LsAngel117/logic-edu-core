package com.logossystemsit.logiceducore.interfaces.rest.academic.assessment.dto.response;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;

import java.math.BigDecimal;
import java.time.Instant;

public record AssessmentResponse(
        String id,
        String groupId,
        String evaluationPeriodId,
        String name,
        String type,
        BigDecimal weight,
        BigDecimal maxScore,
        Instant createdAt,
        Instant updatedAt
) {
    public static AssessmentResponse from(AssessmentResult result) {
        return new AssessmentResponse(
                result.id(),
                result.groupId(),
                result.evaluationPeriodId(),
                result.name(),
                result.type(),
                result.weight(),
                result.maxScore(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
