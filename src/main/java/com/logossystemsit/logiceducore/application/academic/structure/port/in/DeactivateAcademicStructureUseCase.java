package com.logossystemsit.logiceducore.application.academic.structure.port.in;

import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructureId;

public interface DeactivateAcademicStructureUseCase {

    AcademicStructureResult execute(AcademicStructureId id);
}
