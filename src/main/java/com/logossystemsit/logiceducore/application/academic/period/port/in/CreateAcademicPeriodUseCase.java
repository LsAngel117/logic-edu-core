package com.logossystemsit.logiceducore.application.academic.period.port.in;

import com.logossystemsit.logiceducore.application.academic.period.dto.command.CreateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;

public interface CreateAcademicPeriodUseCase {
    AcademicPeriodResult execute(CreateAcademicPeriodCommand command);
}
