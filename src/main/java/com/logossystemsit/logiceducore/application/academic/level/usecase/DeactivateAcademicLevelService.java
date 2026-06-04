package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.DeactivateAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeactivateAcademicLevelService implements DeactivateAcademicLevelUseCase {

    private final AcademicLevelRepository repository;
    private final Clock clock;

    public DeactivateAcademicLevelService(AcademicLevelRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicLevelResult execute(AcademicLevelId id) {
        AcademicLevel level = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_LEVEL_NOT_FOUND,
                        "AcademicLevel not found: " + id.value()));

        if (level.getStatus().name().equals("INACTIVE")) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "AcademicLevel is already inactive");
        }

        if (repository.existsActivePeriodsByLevelId(id)) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Cannot deactivate level with active academic periods");
        }

        AcademicLevel deactivated = level.deactivate(clock.instant());
        repository.save(deactivated);

        return AcademicLevelResult.from(deactivated);
    }
}
