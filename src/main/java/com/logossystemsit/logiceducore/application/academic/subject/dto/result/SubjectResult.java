package com.logossystemsit.logiceducore.application.academic.subject.dto.result;

import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;

import java.time.Instant;

public record SubjectResult(
        String id,
        String schoolId,
        String code,
        String name,
        String description,
        int hours,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static SubjectResult from(Subject subject) {
        return new SubjectResult(
                subject.getId().value(),
                subject.getSchoolId().value(),
                subject.getCode(),
                subject.getName(),
                subject.getDescription(),
                subject.getHours(),
                subject.getStatus().name(),
                subject.getCreatedAt(),
                subject.getUpdatedAt()
        );
    }
}
