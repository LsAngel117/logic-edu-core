package com.logossystemsit.logiceducore.application.school.dto.command;

import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;

public record UpdateSchoolCommand(
        SchoolId schoolId,
        SchoolName name,
        SchoolCode code,
        SchoolShortName shortName,
        SchoolDescription description,
        SchoolEmail email,
        SchoolPhone phone,
        SchoolAddress address
) {}
