package com.logossystemsit.logiceducore.application.academic.grade.port.in;

import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.AssessmentId;

import java.util.List;

public interface ListGradesByAssessmentUseCase {
    List<GradeResult> execute(AssessmentId assessmentId);
}
