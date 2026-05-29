package com.logossystemsit.logiceducore.application.academic.evaluation.port.in;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.UpdateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;

public interface UpdateEvaluationPeriodUseCase {
    EvaluationPeriodResult execute(UpdateEvaluationPeriodCommand command);
}
