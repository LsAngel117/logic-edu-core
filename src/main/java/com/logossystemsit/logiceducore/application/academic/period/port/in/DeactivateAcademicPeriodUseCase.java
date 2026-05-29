package com.logossystemsit.logiceducore.application.academic.period.port.in;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;

public interface DeactivateAcademicPeriodUseCase {
    AcademicPeriodResult execute(AcademicPeriodId id);
}
