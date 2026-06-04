package com.logossystemsit.logiceducore.application.academic.grade.usecase;

import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.application.academic.grade.port.in.GetGradeUseCase;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

public class GetGradeService implements GetGradeUseCase {

    private final GradeRepository gradeRepository;

    public GetGradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public GradeResult execute(GradeId id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GRADE_NOT_FOUND,
                        "Grade not found: " + id.value()));
        return GradeResult.from(grade);
    }
}
