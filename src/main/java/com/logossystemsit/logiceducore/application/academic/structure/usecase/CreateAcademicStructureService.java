package com.logossystemsit.logiceducore.application.academic.structure.usecase;

import com.logossystemsit.logiceducore.application.academic.structure.dto.command.CreateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.application.academic.structure.port.in.CreateAcademicStructureUseCase;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class CreateAcademicStructureService implements CreateAcademicStructureUseCase {

    private final AcademicStructureRepository repository;
    private final Clock clock;

    public CreateAcademicStructureService(AcademicStructureRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicStructureResult execute(CreateAcademicStructureCommand command) {
        if (repository.findActiveBySchoolId(command.schoolId()).isPresent()) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION,
                    "An active structure already exists for school " + command.schoolId().value());
        }

        AcademicStructure structure = AcademicStructure.create(
                command.structureId(),
                command.schoolId(),
                command.structureType(),
                command.levelsCount(),
                command.periodsPerLevel(),
                command.evaluationPeriodsPerPeriod(),
                command.subjectsPerPeriod(),
                command.hoursPerSubject(),
                clock.instant()
        );

        repository.save(structure);

        return AcademicStructureResult.from(structure);
    }
}
