package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.ListEvaluationPeriodsByPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;

import java.util.List;

public class ListEvaluationPeriodsByPeriodService implements ListEvaluationPeriodsByPeriodUseCase {

    private final EvaluationPeriodRepository repository;

    public ListEvaluationPeriodsByPeriodService(EvaluationPeriodRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EvaluationPeriodResult> execute(AcademicPeriodId periodId) {
        List<EvaluationPeriod> periods = repository.findByPeriodId(periodId);
        return periods.stream()
                .map(EvaluationPeriodResult::from)
                .toList();
    }
}
