package com.logossystemsit.logiceducore.interfaces.rest.academic.enrollment.dto.response;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;

public record EnrollmentResponse(
        String id,
        String userId,
        String groupId,
        String status,
        String enrolledAt,
        String updatedAt
) {
    public static EnrollmentResponse from(EnrollmentResult result) {
        return new EnrollmentResponse(
                result.id(),
                result.userId(),
                result.groupId(),
                result.status(),
                result.enrolledAt() != null ? result.enrolledAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
