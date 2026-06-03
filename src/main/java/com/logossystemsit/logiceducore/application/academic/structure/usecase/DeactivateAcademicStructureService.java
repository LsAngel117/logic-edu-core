package com.logossystemsit.logiceducore.application.academic.structure.usecase;

import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.application.academic.structure.port.in.DeactivateAcademicStructureUseCase;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeactivateAcademicStructureService implements DeactivateAcademicStructureUseCase {

    private final AcademicStructureRepository repository;
    private final Clock clock;

    public DeactivateAcademicStructureService(AcademicStructureRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AcademicStructureResult execute(AcademicStructureId id) {
        AcademicStructure structure = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "AcademicStructure not found: " + id.value()));

        if (!structure.isActive()) {
            throw new IllegalStateException("AcademicStructure is already inactive");
        }

        AcademicStructure deactivated = structure.deactivate(clock.instant());
        repository.save(deactivated);

        return AcademicStructureResult.from(deactivated);
    }
}
