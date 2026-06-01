package com.logossystemsit.logiceducore.application.academic.assessment.dto.result;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;

import java.math.BigDecimal;
import java.time.Instant;

public record AssessmentResult(
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
    public static AssessmentResult from(Assessment assessment) {
        return new AssessmentResult(
                assessment.getId().value(),
                assessment.getGroupId().value(),
                assessment.getEvaluationPeriodId()
                        .map(epId -> epId.value())
                        .orElse(null),
                assessment.getName(),
                assessment.getType().name(),
                assessment.getWeight(),
                assessment.getMaxScore(),
                assessment.getCreatedAt(),
                assessment.getUpdatedAt()
        );
    }
}
