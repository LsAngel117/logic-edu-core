package com.logossystemsit.logiceducore.application.academic.grade.port.in;

import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;

public interface GetGradeUseCase {
    GradeResult execute(GradeId id);
}
