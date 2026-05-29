package com.logossystemsit.logiceducore.application.academic.level.dto.result;

import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;

import java.time.Instant;

public record AcademicLevelResult(
        String id,
        String schoolId,
        String name,
        int number,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static AcademicLevelResult from(AcademicLevel level) {
        return new AcademicLevelResult(
                level.getId().value(),
                level.getSchoolId().value(),
                level.getName(),
                level.getNumber(),
                level.getStatus().name(),
                level.getCreatedAt(),
                level.getUpdatedAt()
        );
    }
}
