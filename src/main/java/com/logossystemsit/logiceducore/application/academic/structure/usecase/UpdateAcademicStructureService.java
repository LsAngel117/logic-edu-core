package com.logossystemsit.logiceducore.application.academic.structure.usecase;

import com.logossystemsit.logiceducore.application.academic.structure.dto.command.UpdateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.application.academic.structure.port.in.UpdateAcademicStructureUseCase;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class UpdateAcademicStructureService implements UpdateAcademicStructureUseCase {

    private final AcademicStructureRepository repository;
    private final Clock clock;

    public UpdateAcademicStructureService(AcademicStructureRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicStructureResult execute(UpdateAcademicStructureCommand command) {
        AcademicStructure current = repository.findById(command.structureId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "AcademicStructure not found: " + command.structureId().value()));

        if (!current.isActive()) {
            throw new IllegalStateException("Cannot modify an inactive structure");
        }

        AcademicStructure deactivated = current.deactivate(clock.instant());
        repository.save(deactivated);

        AcademicStructure newVersion = current.changeVersion(
                command.structureType(),
                command.levelsCount(),
                command.periodsPerLevel(),
                command.evaluationPeriodsPerPeriod(),
                command.subjectsPerPeriod(),
                command.hoursPerSubject(),
                clock.instant()
        );

        repository.save(newVersion);

        return AcademicStructureResult.from(newVersion);
    }
}
