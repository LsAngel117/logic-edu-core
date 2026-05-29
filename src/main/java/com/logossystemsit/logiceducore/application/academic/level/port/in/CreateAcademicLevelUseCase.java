package com.logossystemsit.logiceducore.application.academic.level.port.in;

import com.logossystemsit.logiceducore.application.academic.level.dto.command.CreateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;

public interface CreateAcademicLevelUseCase {

    AcademicLevelResult execute(CreateAcademicLevelCommand command);
}
