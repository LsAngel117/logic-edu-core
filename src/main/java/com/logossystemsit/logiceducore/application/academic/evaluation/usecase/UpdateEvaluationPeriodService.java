package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.UpdateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.UpdateEvaluationPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

public class UpdateEvaluationPeriodService implements UpdateEvaluationPeriodUseCase {

    private final EvaluationPeriodRepository repository;
    private final Clock clock;

    public UpdateEvaluationPeriodService(EvaluationPeriodRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public EvaluationPeriodResult execute(UpdateEvaluationPeriodCommand command) {
        EvaluationPeriod current = repository.findById(command.evaluationPeriodId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "EvaluationPeriod not found: " + command.evaluationPeriodId().value()));

        if (current.getStatus() == EvaluationPeriodStatus.INACTIVE) {
            throw new IllegalStateException("Cannot modify an inactive EvaluationPeriod");
        }

        EvaluationPeriod updated = current;
        Instant now = clock.instant();

        if (command.name() != null && !command.name().equals(current.getName())) {
            updated = updated.changeName(command.name(), now);
        }

        if (command.weight() != null) {
            BigDecimal newWeight = command.weight();
            if (newWeight.compareTo(current.getWeight()) != 0) {
                BigDecimal sumWithoutCurrent = repository.sumWeightsByPeriodId(command.periodId())
                        .subtract(current.getWeight());
                BigDecimal newTotal = sumWithoutCurrent.add(newWeight);
                if (newTotal.compareTo(new BigDecimal("100")) > 0) {
                    throw new IllegalArgumentException(
                            "weight sum would exceed 100 for period: " + command.periodId().value());
                }
                updated = updated.changeWeight(newWeight, now);
            }
        }

        if (command.startDate() != null || command.endDate() != null) {
            var newStart = command.startDate() != null ? command.startDate() : current.getStartDate();
            var newEnd = command.endDate() != null ? command.endDate() : current.getEndDate();
            updated = updated.changeDates(newStart, newEnd, now);
        }

        repository.save(updated);
        return EvaluationPeriodResult.from(updated);
    }
}
