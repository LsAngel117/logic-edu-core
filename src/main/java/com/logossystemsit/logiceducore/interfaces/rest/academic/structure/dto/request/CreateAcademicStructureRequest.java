package com.logossystemsit.logiceducore.interfaces.rest.academic.structure.dto.request;

public record CreateAcademicStructureRequest(
        String structureType,
        int levelsCount,
        int periodsPerLevel,
        int evaluationPeriodsPerPeriod,
        int subjectsPerPeriod,
        int hoursPerSubject
) {}
