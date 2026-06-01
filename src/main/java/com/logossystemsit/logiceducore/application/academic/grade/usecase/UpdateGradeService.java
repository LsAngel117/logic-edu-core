package com.logossystemsit.logiceducore.application.academic.grade.usecase;

import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.grade.dto.command.UpdateGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.application.academic.grade.port.in.UpdateGradeUseCase;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;
import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class UpdateGradeService implements UpdateGradeUseCase {

    private final GradeRepository gradeRepository;
    private final AssessmentRepository assessmentRepository;
    private final GroupRepository groupRepository;
    private final Clock clock;

    public UpdateGradeService(
            GradeRepository gradeRepository,
            AssessmentRepository assessmentRepository,
            GroupRepository groupRepository,
            Clock clock
    ) {
        this.gradeRepository = gradeRepository;
        this.assessmentRepository = assessmentRepository;
        this.groupRepository = groupRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public GradeResult execute(UpdateGradeCommand command) {
        // 1. Find existing grade
        Grade existing = gradeRepository.findByAssessmentIdAndStudentId(
                        command.assessmentId(), command.studentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Grade not found for assessment " + command.assessmentId().value()
                                + " and student " + command.studentId().value()));

        // 2. Load Assessment
        Assessment assessment = assessmentRepository.findById(command.assessmentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Assessment not found: " + command.assessmentId().value()));

        // 3. Cross-aggregate auth
        Group group = groupRepository.findById(assessment.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Group not found: " + assessment.getGroupId().value()));

        if (!group.getTeacherId().equals(command.teacherId())) {
            throw new IllegalStateException("Teacher is not authorized for this group");
        }

        // 4. Validate value <= maxScore
        if (command.value().compareTo(assessment.getMaxScore()) > 0) {
            throw new IllegalStateException("Grade value exceeds max score of " + assessment.getMaxScore());
        }

        // 5. Change value
        Grade updated = existing.changeValue(command.value(), clock.instant());
        gradeRepository.save(updated);

        return GradeResult.from(updated);
    }
}
