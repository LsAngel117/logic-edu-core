package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.GetEvaluationPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

public class GetEvaluationPeriodService implements GetEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository repository;

    public GetEvaluationPeriodService(EvaluationPeriodRepository repository) {
        this.repository = repository;
    }

    @Override
    public EvaluationPeriodResult execute(EvaluationPeriodId id) {
        EvaluationPeriod period = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EVALUATION_PERIOD_NOT_FOUND,
                        "EvaluationPeriod not found: " + id.value()));
        return EvaluationPeriodResult.from(period);
    }
}
