package com.logossystemsit.logiceducore.interfaces.rest.academic.grade.dto.response;

import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;

import java.math.BigDecimal;
import java.time.Instant;

public record GradeResponse(
        String id,
        String assessmentId,
        String studentId,
        BigDecimal value,
        Instant gradedAt,
        Instant updatedAt
) {
    public static GradeResponse from(GradeResult result) {
        return new GradeResponse(
                result.id(),
                result.assessmentId(),
                result.studentId(),
                result.value(),
                result.gradedAt(),
                result.updatedAt()
        );
    }
}
