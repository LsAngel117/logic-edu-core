package com.logossystemsit.logiceducore.application.academic.assessment.usecase;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.application.academic.assessment.port.in.ListAssessmentsByGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListAssessmentsByGroupService implements ListAssessmentsByGroupUseCase {

    private final AssessmentRepository assessmentRepository;

    public ListAssessmentsByGroupService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentResult> execute(GroupId groupId) {
        return assessmentRepository.findByGroupId(groupId).stream()
                .map(AssessmentResult::from)
                .toList();
    }
}
