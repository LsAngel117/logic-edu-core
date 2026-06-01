package com.logossystemsit.logiceducore.interfaces.rest.academic.subject.dto.response;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;

public record SubjectResponse(
        String id,
        String schoolId,
        String code,
        String name,
        String description,
        int hours,
        String status,
        String createdAt,
        String updatedAt
) {
    public static SubjectResponse from(SubjectResult result) {
        return new SubjectResponse(
                result.id(),
                result.schoolId(),
                result.code(),
                result.name(),
                result.description(),
                result.hours(),
                result.status(),
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
