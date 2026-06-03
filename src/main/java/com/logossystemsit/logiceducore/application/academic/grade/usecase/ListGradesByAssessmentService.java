package com.logossystemsit.logiceducore.application.academic.grade.usecase;

import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.application.academic.grade.port.in.ListGradesByAssessmentUseCase;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;

import java.util.List;

public class ListGradesByAssessmentService implements ListGradesByAssessmentUseCase {

    private final GradeRepository gradeRepository;

    public ListGradesByAssessmentService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public List<GradeResult> execute(AssessmentId assessmentId) {
        return gradeRepository.findByAssessmentId(assessmentId).stream()
                .map(GradeResult::from)
                .toList();
    }
}
