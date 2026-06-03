package com.logossystemsit.logiceducore.application.academic.structure.port.in;

import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

public interface GetAcademicStructureUseCase {

    AcademicStructureResult execute(AcademicStructureId id);

    AcademicStructureResult findActiveBySchoolId(SchoolId schoolId);
}
