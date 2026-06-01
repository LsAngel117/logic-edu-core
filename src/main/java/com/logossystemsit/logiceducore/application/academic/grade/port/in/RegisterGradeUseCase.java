package com.logossystemsit.logiceducore.application.academic.grade.port.in;

import com.logossystemsit.logiceducore.application.academic.grade.dto.command.RegisterGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;

public interface RegisterGradeUseCase {
    GradeResult execute(RegisterGradeCommand command);
}
