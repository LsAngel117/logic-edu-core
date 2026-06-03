package com.logossystemsit.logiceducore.application.academic.structure.dto.command;

import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.StructureType;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

public record UpdateAcademicStructureCommand(
        AcademicStructureId structureId,
        SchoolId schoolId,
        StructureType structureType,
        int levelsCount,
        int periodsPerLevel,
        int evaluationPeriodsPerPeriod,
        int subjectsPerPeriod,
        int hoursPerSubject
) {}
