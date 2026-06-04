package com.logossystemsit.logiceducore.application.academic.assessment.usecase;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.UpdateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.application.academic.assessment.port.in.UpdateAssessmentUseCase;
import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class UpdateAssessmentService implements UpdateAssessmentUseCase {

    private final AssessmentRepository assessmentRepository;
    private final GroupRepository groupRepository;
    private final Clock clock;

    public UpdateAssessmentService(
            AssessmentRepository assessmentRepository,
            GroupRepository groupRepository,
            Clock clock
    ) {
        this.assessmentRepository = assessmentRepository;
        this.groupRepository = groupRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AssessmentResult execute(UpdateAssessmentCommand command) {
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GROUP_NOT_FOUND,
                        "Group not found: " + command.groupId().value()));

        if (!group.getTeacherId().equals(command.teacherId())) {
            throw new BusinessRuleException(ErrorCode.TEACHER_NOT_ASSIGNED, "Teacher is not authorized for this group");
        }

        Assessment assessment = assessmentRepository.findById(command.assessmentId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ASSESSMENT_NOT_FOUND,
                        "Assessment not found: " + command.assessmentId().value()));

        Assessment changed = assessment.changeData(
                command.name(),
                command.type(),
                command.weight(),
                command.maxScore(),
                command.evaluationPeriodId(),
                clock.instant()
        );

        assessmentRepository.save(changed);

        return AssessmentResult.from(changed);
    }
}
