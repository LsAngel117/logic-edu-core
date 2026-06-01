package com.logossystemsit.logiceducore.application.academic.grade.dto.result;

import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;

import java.math.BigDecimal;
import java.time.Instant;

public record GradeResult(
        String id,
        String assessmentId,
        String studentId,
        BigDecimal value,
        Instant gradedAt,
        Instant updatedAt
) {
    public static GradeResult from(Grade grade) {
        return new GradeResult(
                grade.getId().value(),
                grade.getAssessmentId().value(),
                grade.getStudentId().value(),
                grade.getValue(),
                grade.getGradedAt(),
                grade.getUpdatedAt()
        );
    }
}
