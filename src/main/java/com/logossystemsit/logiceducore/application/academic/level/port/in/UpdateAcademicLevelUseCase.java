package com.logossystemsit.logiceducore.application.academic.level.port.in;

import com.logossystemsit.logiceducore.application.academic.level.dto.command.UpdateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;

public interface UpdateAcademicLevelUseCase {

    AcademicLevelResult execute(UpdateAcademicLevelCommand command);
}
