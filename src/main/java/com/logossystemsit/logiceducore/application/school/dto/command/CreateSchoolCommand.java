package com.logossystemsit.logiceducore.application.school.dto.command;

import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
import com.logossystemsit.logiceducore.shared.valueobject.City;
import com.logossystemsit.logiceducore.shared.valueobject.Country;

public record CreateSchoolCommand(
        SchoolId schoolId,
        SchoolName name,
        SchoolCode code,
        SchoolShortName shortName,
        SchoolDescription description,
        SchoolEmail email,
        SchoolPhone phone,
        SchoolAddress address,
        City city,
        Country country
) {}
