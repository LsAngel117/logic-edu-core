package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.GetAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;

public class GetAcademicLevelService implements GetAcademicLevelUseCase {

    private final AcademicLevelRepository repository;

    public GetAcademicLevelService(AcademicLevelRepository repository) {
        this.repository = repository;
    }

    @Override
    public AcademicLevelResult execute(AcademicLevelId id) {
        return repository.findById(id)
                .map(AcademicLevelResult::from)
                .orElseThrow(() -> new IllegalArgumentException(
                        "AcademicLevel not found: " + id.value()));
    }
}
