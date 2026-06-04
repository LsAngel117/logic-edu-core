package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.command.UpdateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.UpdateAcademicPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodStatus;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class UpdateAcademicPeriodService implements UpdateAcademicPeriodUseCase {

    private final AcademicPeriodRepository repository;
    private final Clock clock;

    public UpdateAcademicPeriodService(AcademicPeriodRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicPeriodResult execute(UpdateAcademicPeriodCommand command) {
        AcademicPeriod current = repository.findById(command.periodId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_PERIOD_NOT_FOUND,
                        "AcademicPeriod not found: " + command.periodId().value()));

        if (current.getStatus() == PeriodStatus.INACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Cannot modify an inactive AcademicPeriod");
        }

        AcademicPeriod updated = current;
        Instant now = clock.instant();
        boolean datesChanged = false;

        if (command.name() != null && !command.name().equals(current.getName())) {
            updated = updated.changeName(command.name(), now);
        }

        if (command.startDate() != null || command.endDate() != null) {
            var newStart = command.startDate() != null ? command.startDate() : current.getStartDate();
            var newEnd = command.endDate() != null ? command.endDate() : current.getEndDate();
            updated = updated.changeDates(newStart, newEnd, now);
            datesChanged = true;
        }

        if (datesChanged) {
            List<AcademicPeriod> others = repository.findByLevelId(command.levelId()).stream()
                    .filter(p -> !p.getId().value().equals(command.periodId().value()))
                    .toList();

            for (AcademicPeriod other : others) {
                if (overlaps(updated.getStartDate(), updated.getEndDate(),
                        other.getStartDate(), other.getEndDate())) {
                    throw new BusinessRuleException(ErrorCode.ACADEMIC_PERIOD_OVERLAP,
                            "Period overlaps with existing period: " + other.getId().value());
                }
            }
        }

        repository.save(updated);

        return AcademicPeriodResult.from(updated);
    }

    private boolean overlaps(java.time.LocalDate newStart, java.time.LocalDate newEnd,
                             java.time.LocalDate existingStart, java.time.LocalDate existingEnd) {
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }
}
