package com.logossystemsit.logiceducore.application.academic.evaluation.port.in;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.CreateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;

public interface CreateEvaluationPeriodUseCase {
    EvaluationPeriodResult execute(CreateEvaluationPeriodCommand command);
}
