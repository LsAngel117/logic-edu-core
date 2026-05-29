package com.logossystemsit.logiceducore.application.school.port.in;

import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

public interface DeactivateSchoolUseCase {

    SchoolResult execute(SchoolId schoolId);
}
