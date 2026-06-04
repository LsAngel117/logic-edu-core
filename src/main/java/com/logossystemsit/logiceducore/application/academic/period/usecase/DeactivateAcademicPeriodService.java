package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.DeactivateAcademicPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodStatus;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeactivateAcademicPeriodService implements DeactivateAcademicPeriodUseCase {

    private final AcademicPeriodRepository repository;
    private final Clock clock;

    public DeactivateAcademicPeriodService(AcademicPeriodRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicPeriodResult execute(AcademicPeriodId id) {
        AcademicPeriod period = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_PERIOD_NOT_FOUND,
                        "AcademicPeriod not found: " + id.value()));

        if (period.getStatus() == PeriodStatus.INACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "AcademicPeriod is already inactive");
        }

        if (repository.existsActiveEvaluationPeriodsByPeriodId(id)) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Cannot deactivate period with active evaluation periods");
        }

        AcademicPeriod deactivated = period.deactivate(clock.instant());
        repository.save(deactivated);

        return AcademicPeriodResult.from(deactivated);
    }
}
