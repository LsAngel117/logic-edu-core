package com.logossystemsit.logiceducore.application.academic.assessment.port.in;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.UpdateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;

public interface UpdateAssessmentUseCase {
    AssessmentResult execute(UpdateAssessmentCommand command);
}
