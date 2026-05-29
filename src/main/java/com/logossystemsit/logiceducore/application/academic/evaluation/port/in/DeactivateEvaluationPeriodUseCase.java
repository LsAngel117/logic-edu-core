package com.logossystemsit.logiceducore.application.academic.evaluation.port.in;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;

public interface DeactivateEvaluationPeriodUseCase {
    EvaluationPeriodResult execute(EvaluationPeriodId id);
}
