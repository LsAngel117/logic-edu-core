package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.command.CreateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.CreateAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class CreateAcademicLevelService implements CreateAcademicLevelUseCase {

    private final AcademicLevelRepository repository;
    private final Clock clock;

    public CreateAcademicLevelService(AcademicLevelRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicLevelResult execute(CreateAcademicLevelCommand command) {
        if (repository.existsBySchoolIdAndNumber(command.schoolId(), command.number())) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION,
                    "Level number " + command.number() + " already exists for school " + command.schoolId().value());
        }

        AcademicLevel level = AcademicLevel.create(
                command.levelId(),
                command.schoolId(),
                command.name(),
                command.number(),
                clock.instant()
        );

        repository.save(level);

        return AcademicLevelResult.from(level);
    }
}
