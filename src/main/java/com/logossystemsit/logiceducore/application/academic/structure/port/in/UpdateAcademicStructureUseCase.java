package com.logossystemsit.logiceducore.application.academic.structure.port.in;

import com.logossystemsit.logiceducore.application.academic.structure.dto.command.UpdateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;

public interface UpdateAcademicStructureUseCase {

    AcademicStructureResult execute(UpdateAcademicStructureCommand command);
}
