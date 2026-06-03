package com.logossystemsit.logiceducore.application.academic.level.dto.command;

import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

public record CreateAcademicLevelCommand(
        AcademicLevelId levelId,
        SchoolId schoolId,
        String name,
        int number
) {}
