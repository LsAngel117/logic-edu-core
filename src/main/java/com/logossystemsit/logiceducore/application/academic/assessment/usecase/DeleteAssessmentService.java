package com.logossystemsit.logiceducore.application.academic.assessment.usecase;

import com.logossystemsit.logiceducore.application.academic.assessment.port.in.DeleteAssessmentUseCase;
import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeleteAssessmentService implements DeleteAssessmentUseCase {

    private final AssessmentRepository assessmentRepository;
    private final GroupRepository groupRepository;
    private final GradeRepository gradeRepository;
    private final Clock clock;

    public DeleteAssessmentService(
            AssessmentRepository assessmentRepository,
            GroupRepository groupRepository,
            GradeRepository gradeRepository,
            Clock clock
    ) {
        this.assessmentRepository = assessmentRepository;
        this.groupRepository = groupRepository;
        this.gradeRepository = gradeRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void execute(AssessmentId id, GroupId groupId, UserId teacherId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Group not found: " + groupId.value()));

        if (!group.getTeacherId().equals(teacherId)) {
            throw new IllegalStateException("Teacher is not authorized for this group");
        }

        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Assessment not found: " + id.value()));

        if (gradeRepository.countByAssessmentId(id) > 0) {
            throw new IllegalStateException("Cannot delete: assessment has grades");
        }

        assessmentRepository.delete(assessment);
    }
}
