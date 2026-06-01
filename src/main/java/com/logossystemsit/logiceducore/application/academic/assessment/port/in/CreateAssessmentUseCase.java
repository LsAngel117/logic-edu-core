package com.logossystemsit.logiceducore.application.academic.assessment.port.in;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.CreateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;

public interface CreateAssessmentUseCase {
    AssessmentResult execute(CreateAssessmentCommand command);
}
