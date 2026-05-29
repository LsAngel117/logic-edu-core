package com.logossystemsit.logiceducore.application.academic.level.port.in;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;

public interface ListAcademicLevelsBySchoolUseCase {

    List<AcademicLevelResult> execute(SchoolId schoolId);
}
