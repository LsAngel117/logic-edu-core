package com.logossystemsit.logiceducore.application.academic.level.dto.command;

import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

public record UpdateAcademicLevelCommand(
        AcademicLevelId levelId,
        SchoolId schoolId,
        String name,
        int number
) {}
