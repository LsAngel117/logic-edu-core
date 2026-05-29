package com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.response;

import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;

import java.time.Instant;

public record AcademicStructureResponse(
        String id,
        String schoolId,
        String structureType,
        int levelsCount,
        int periodsPerLevel,
        int evaluationPeriodsPerPeriod,
        int subjectsPerPeriod,
        int hoursPerSubject,
        boolean active,
        int version,
        String createdAt,
        String updatedAt
) {
    public static AcademicStructureResponse from(AcademicStructureResult result) {
        return new AcademicStructureResponse(
                result.id(),
                result.schoolId(),
                result.structureType(),
                result.levelsCount(),
                result.periodsPerLevel(),
                result.evaluationPeriodsPerPeriod(),
                result.subjectsPerPeriod(),
                result.hoursPerSubject(),
                result.active(),
                result.version(),
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }
}
