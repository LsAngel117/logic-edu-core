package com.logossystemsit.logiceducore.application.academic.level.port.in;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;

public interface GetAcademicLevelUseCase {

    AcademicLevelResult execute(AcademicLevelId id);
}
