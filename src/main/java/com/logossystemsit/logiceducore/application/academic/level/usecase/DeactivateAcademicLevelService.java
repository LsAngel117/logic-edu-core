package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.DeactivateAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
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
                .orElseThrow(() -> new IllegalArgumentException(
                        "AcademicLevel not found: " + id.value()));

        if (level.getStatus().name().equals("INACTIVE")) {
            throw new IllegalStateException("AcademicLevel is already inactive");
        }

        if (repository.existsActivePeriodsByLevelId(id)) {
            throw new IllegalStateException("Cannot deactivate level with active academic periods");
        }

        AcademicLevel deactivated = level.deactivate(clock.instant());
        repository.save(deactivated);

        return AcademicLevelResult.from(deactivated);
    }
}
