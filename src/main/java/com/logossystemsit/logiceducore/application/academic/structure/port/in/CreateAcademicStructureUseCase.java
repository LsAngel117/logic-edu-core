package com.logossystemsit.logiceducore.application.academic.structure.port.in;

import com.logossystemsit.logiceducore.application.academic.structure.dto.command.CreateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;

public interface CreateAcademicStructureUseCase {

    AcademicStructureResult execute(CreateAcademicStructureCommand command);
}
