package com.logossystemsit.logiceducore.application.academic.grade.usecase;

import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.application.academic.grade.port.in.GetGradeUseCase;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;

public class GetGradeService implements GetGradeUseCase {

    private final GradeRepository gradeRepository;

    public GetGradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public GradeResult execute(GradeId id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Grade not found: " + id.value()));
        return GradeResult.from(grade);
    }
}
