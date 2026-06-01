package com.logossystemsit.logiceducore.application.academic.enrollment.dto.result;

import com.logossystemsit.logiceducore.domain.academic.enrollment.model.Enrollment;

import java.time.Instant;

public record EnrollmentResult(
        String id,
        String userId,
        String groupId,
        String status,
        Instant enrolledAt,
        Instant updatedAt
) {
    public static EnrollmentResult from(Enrollment enrollment) {
        return new EnrollmentResult(
                enrollment.getId().value(),
                enrollment.getUserId().value(),
                enrollment.getGroupId().value(),
                enrollment.getStatus().name(),
                enrollment.getEnrolledAt(),
                enrollment.getUpdatedAt()
        );
    }
}
