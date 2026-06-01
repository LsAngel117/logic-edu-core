package com.logossystemsit.logiceducore.application.academic.assessment.usecase;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.application.academic.assessment.port.in.GetAssessmentUseCase;
import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.AssessmentId;
import org.springframework.transaction.annotation.Transactional;

public class GetAssessmentService implements GetAssessmentUseCase {

    private final AssessmentRepository assessmentRepository;

    public GetAssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentResult execute(AssessmentId id) {
        return assessmentRepository.findById(id)
                .map(AssessmentResult::from)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Assessment not found: " + id.value()));
    }
}
