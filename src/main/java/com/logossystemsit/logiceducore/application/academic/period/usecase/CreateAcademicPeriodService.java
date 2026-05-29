package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.command.CreateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.CreateAcademicPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

public class CreateAcademicPeriodService implements CreateAcademicPeriodUseCase {

    private final AcademicPeriodRepository repository;
    private final Clock clock;

    public CreateAcademicPeriodService(AcademicPeriodRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicPeriodResult execute(CreateAcademicPeriodCommand command) {
        List<AcademicPeriod> existingPeriods = repository.findByLevelId(command.levelId());

        for (AcademicPeriod existing : existingPeriods) {
            if (overlaps(command.startDate(), command.endDate(),
                    existing.getStartDate(), existing.getEndDate())) {
                throw new IllegalArgumentException(
                        "Period overlaps with existing period: " + existing.getId().value());
            }
        }

        AcademicPeriod period = AcademicPeriod.create(
                command.periodId(),
                command.levelId(),
                command.periodType(),
                command.name(),
                command.sequence(),
                command.startDate(),
                command.endDate(),
                clock.instant()
        );

        repository.save(period);

        return AcademicPeriodResult.from(period);
    }

    private boolean overlaps(java.time.LocalDate newStart, java.time.LocalDate newEnd,
                             java.time.LocalDate existingStart, java.time.LocalDate existingEnd) {
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }
}
