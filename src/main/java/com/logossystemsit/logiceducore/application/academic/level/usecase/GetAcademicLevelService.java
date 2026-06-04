package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.GetAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

public class GetAcademicLevelService implements GetAcademicLevelUseCase {

    private final AcademicLevelRepository repository;

    public GetAcademicLevelService(AcademicLevelRepository repository) {
        this.repository = repository;
    }

    @Override
    public AcademicLevelResult execute(AcademicLevelId id) {
        return repository.findById(id)
                .map(AcademicLevelResult::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_LEVEL_NOT_FOUND,
                        "AcademicLevel not found: " + id.value()));
    }
}
