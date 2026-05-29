package com.logossystemsit.logiceducore.application.academic.period.port.in;

import com.logossystemsit.logiceducore.application.academic.period.dto.command.UpdateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;

public interface UpdateAcademicPeriodUseCase {
    AcademicPeriodResult execute(UpdateAcademicPeriodCommand command);
}
