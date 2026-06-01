package com.logossystemsit.logiceducore.application.academic.grade.port.in;

import com.logossystemsit.logiceducore.application.academic.grade.dto.command.UpdateGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;

public interface UpdateGradeUseCase {
    GradeResult execute(UpdateGradeCommand command);
}
