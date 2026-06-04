package com.logossystemsit.logiceducore.application.academic.structure.usecase;

import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.application.academic.structure.port.in.GetAcademicStructureUseCase;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

public class GetAcademicStructureService implements GetAcademicStructureUseCase {

    private final AcademicStructureRepository repository;

    public GetAcademicStructureService(AcademicStructureRepository repository) {
        this.repository = repository;
    }

    @Override
    public AcademicStructureResult execute(AcademicStructureId id) {
        return repository.findById(id)
                .map(AcademicStructureResult::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_STRUCTURE_NOT_FOUND,
                        "AcademicStructure not found: " + id.value()));
    }

    @Override
    public AcademicStructureResult findActiveBySchoolId(SchoolId schoolId) {
        return repository.findActiveBySchoolId(schoolId)
                .map(AcademicStructureResult::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_STRUCTURE_NOT_FOUND,
                        "No active AcademicStructure found for school: " + schoolId.value()));
    }
}
