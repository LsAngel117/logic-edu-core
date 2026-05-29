package com.logossystemsit.logiceducore.application.academic.period.port.in;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;

import java.util.List;

public interface ListAcademicPeriodsByLevelUseCase {
    List<AcademicPeriodResult> execute(AcademicLevelId levelId);
}
