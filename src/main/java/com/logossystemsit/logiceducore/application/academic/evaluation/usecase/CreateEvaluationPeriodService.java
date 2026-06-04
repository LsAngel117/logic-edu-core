package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.CreateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.CreateEvaluationPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;

public class CreateEvaluationPeriodService implements CreateEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository evaluationRepository;
    private final AcademicPeriodRepository periodRepository;
    private final Clock clock;

    public CreateEvaluationPeriodService(
            EvaluationPeriodRepository evaluationRepository,
            AcademicPeriodRepository periodRepository,
            Clock clock) {
        this.evaluationRepository = evaluationRepository;
        this.periodRepository = periodRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public EvaluationPeriodResult execute(CreateEvaluationPeriodCommand command) {
        AcademicPeriod period = periodRepository.findById(command.periodId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_PERIOD_NOT_FOUND,
                        "AcademicPeriod not found: " + command.periodId().value()));

        BigDecimal currentSum = evaluationRepository.sumWeightsByPeriodId(command.periodId());
        BigDecimal newTotal = currentSum.add(command.weight());

        if (newTotal.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION,
                    "weight sum would exceed 100 for period: " + command.periodId().value());
        }

        EvaluationPeriod evaluation = EvaluationPeriod.create(
                command.evaluationPeriodId(),
                command.periodId(),
                command.name(),
                command.sequence(),
                command.weight(),
                command.startDate(),
                command.endDate(),
                clock.instant()
        );

        evaluationRepository.save(evaluation);

        return EvaluationPeriodResult.from(evaluation);
    }
}
