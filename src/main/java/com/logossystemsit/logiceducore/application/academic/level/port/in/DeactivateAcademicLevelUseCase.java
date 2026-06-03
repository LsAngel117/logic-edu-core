package com.logossystemsit.logiceducore.application.academic.level.port.in;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;

public interface DeactivateAcademicLevelUseCase {

    AcademicLevelResult execute(AcademicLevelId id);
}
