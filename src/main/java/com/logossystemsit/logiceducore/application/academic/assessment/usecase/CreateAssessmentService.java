package com.logossystemsit.logiceducore.application.academic.assessment.usecase;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.CreateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.application.academic.assessment.port.in.CreateAssessmentUseCase;
import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class CreateAssessmentService implements CreateAssessmentUseCase {

    private final AssessmentRepository assessmentRepository;
    private final GroupRepository groupRepository;
    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final Clock clock;

    public CreateAssessmentService(
            AssessmentRepository assessmentRepository,
            GroupRepository groupRepository,
            EvaluationPeriodRepository evaluationPeriodRepository,
            Clock clock
    ) {
        this.assessmentRepository = assessmentRepository;
        this.groupRepository = groupRepository;
        this.evaluationPeriodRepository = evaluationPeriodRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AssessmentResult execute(CreateAssessmentCommand command) {
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Group not found: " + command.groupId().value()));

        if (group.getStatus() != GroupStatus.ACTIVE) {
            throw new IllegalStateException("Cannot create assessment: group is not active");
        }

        if (!group.getTeacherId().equals(command.teacherId())) {
            throw new IllegalStateException("Teacher is not authorized for this group");
        }

        if (assessmentRepository.existsByGroupIdAndName(command.groupId(), command.name())) {
            throw new IllegalStateException("Assessment with name '" + command.name() + "' already exists in this group");
        }

        if (command.evaluationPeriodId() != null) {
            evaluationPeriodRepository.findById(command.evaluationPeriodId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Evaluation period not found: " + command.evaluationPeriodId().value()));
        }

        Assessment assessment = Assessment.create(
                AssessmentId.generate(),
                command.groupId(),
                command.evaluationPeriodId(),
                command.name(),
                command.type(),
                command.weight(),
                command.maxScore(),
                clock.instant()
        );

        assessmentRepository.save(assessment);

        return AssessmentResult.from(assessment);
    }
}
