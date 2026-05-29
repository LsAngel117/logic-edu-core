package com.logossystemsit.logiceducore.interfaces.rest.academic.level.dto.response;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;

public record AcademicLevelResponse(
        String id,
        String schoolId,
        String name,
        int number,
        String status,
        String createdAt,
        String updatedAt
) {
    public static AcademicLevelResponse from(AcademicLevelResult result) {
        return new AcademicLevelResponse(
                result.id(),
                result.schoolId(),
                result.name(),
                result.number(),
                result.status(),
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
