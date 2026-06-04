package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.command.UpdateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.UpdateAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class UpdateAcademicLevelService implements UpdateAcademicLevelUseCase {

    private final AcademicLevelRepository repository;
    private final Clock clock;

    public UpdateAcademicLevelService(AcademicLevelRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicLevelResult execute(UpdateAcademicLevelCommand command) {
        AcademicLevel current = repository.findById(command.levelId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_LEVEL_NOT_FOUND,
                        "AcademicLevel not found: " + command.levelId().value()));

        if (current.getStatus().name().equals("INACTIVE")) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Cannot modify an inactive AcademicLevel");
        }

        AcademicLevel updated = current;

        if (command.number() != current.getNumber()) {
            if (repository.existsBySchoolIdAndNumber(command.schoolId(), command.number())) {
                throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION,
                        "Level number " + command.number() + " already exists for school " + command.schoolId().value());
            }
            updated = updated.changeNumber(command.number(), clock.instant());
        }

        if (!command.name().equals(current.getName())) {
            updated = updated.changeName(command.name(), clock.instant());
        }

        repository.save(updated);

        return AcademicLevelResult.from(updated);
    }
}
