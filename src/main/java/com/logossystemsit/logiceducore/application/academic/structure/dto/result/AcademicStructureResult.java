package com.logossystemsit.logiceducore.application.academic.structure.dto.result;

import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;

import java.time.Instant;

public record AcademicStructureResult(
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
        Instant createdAt,
        Instant updatedAt
) {
    public static AcademicStructureResult from(AcademicStructure structure) {
        return new AcademicStructureResult(
                structure.getId().value(),
                structure.getSchoolId().value(),
                structure.getStructureType().name(),
                structure.getLevelsCount(),
                structure.getPeriodsPerLevel(),
                structure.getEvaluationPeriodsPerPeriod(),
                structure.getSubjectsPerPeriod(),
                structure.getHoursPerSubject(),
                structure.isActive(),
                structure.getVersion(),
                structure.getCreatedAt(),
                structure.getUpdatedAt()
        );
    }
}
