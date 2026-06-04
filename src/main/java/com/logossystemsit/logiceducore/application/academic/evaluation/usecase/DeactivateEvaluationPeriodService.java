package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.DeactivateEvaluationPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodStatus;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeactivateEvaluationPeriodService implements DeactivateEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository repository;
    private final Clock clock;

    public DeactivateEvaluationPeriodService(EvaluationPeriodRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public EvaluationPeriodResult execute(EvaluationPeriodId id) {
        EvaluationPeriod period = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EVALUATION_PERIOD_NOT_FOUND,
                        "EvaluationPeriod not found: " + id.value()));

        if (period.getStatus() == EvaluationPeriodStatus.INACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "EvaluationPeriod is already inactive");
        }

        EvaluationPeriod deactivated = period.deactivate(clock.instant());
        repository.save(deactivated);

        return EvaluationPeriodResult.from(deactivated);
    }
}
