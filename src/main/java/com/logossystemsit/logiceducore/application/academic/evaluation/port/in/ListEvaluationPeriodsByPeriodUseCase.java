package com.logossystemsit.logiceducore.application.academic.evaluation.port.in;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;

import java.util.List;

public interface ListEvaluationPeriodsByPeriodUseCase {
    List<EvaluationPeriodResult> execute(AcademicPeriodId periodId);
}
