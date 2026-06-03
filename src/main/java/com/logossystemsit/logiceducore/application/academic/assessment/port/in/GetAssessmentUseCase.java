package com.logossystemsit.logiceducore.application.academic.assessment.port.in;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;

public interface GetAssessmentUseCase {
    AssessmentResult execute(AssessmentId id);
}
