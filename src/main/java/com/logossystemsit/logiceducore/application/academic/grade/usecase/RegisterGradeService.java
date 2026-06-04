package com.logossystemsit.logiceducore.application.academic.grade.usecase;

import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.grade.dto.command.RegisterGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.application.academic.grade.port.in.RegisterGradeUseCase;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;
import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class RegisterGradeService implements RegisterGradeUseCase {

    private final GradeRepository gradeRepository;
    private final AssessmentRepository assessmentRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public RegisterGradeService(
            GradeRepository gradeRepository,
            AssessmentRepository assessmentRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.gradeRepository = gradeRepository;
        this.assessmentRepository = assessmentRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public GradeResult execute(RegisterGradeCommand command) {
        // 1. Load Assessment
        Assessment assessment = assessmentRepository.findById(command.assessmentId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ASSESSMENT_NOT_FOUND,
                        "Assessment not found: " + command.assessmentId().value()));

        // 2. Cross-aggregate auth: load Group via assessment.getGroupId()
        Group group = groupRepository.findById(assessment.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GROUP_NOT_FOUND,
                        "Group not found: " + assessment.getGroupId().value()));

        // 3. Validate teacher authorization
        if (!group.getTeacherId().equals(command.teacherId())) {
            throw new BusinessRuleException(ErrorCode.TEACHER_NOT_ASSIGNED, "Teacher is not authorized for this group");
        }

        // 4. Validate student exists and is active
        User student = userRepository.findById(command.studentId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                        "Student not found: " + command.studentId().value()));

        if (!student.isActive()) {
            throw new BusinessRuleException(ErrorCode.USER_INACTIVE, "Cannot register grade: student is not active");
        }

        // 5. Validate value <= assessment.maxScore
        if (command.value().compareTo(assessment.getMaxScore()) > 0) {
            throw new BusinessRuleException(ErrorCode.GRADE_EXCEEDS_MAX_SCORE, "Grade value exceeds max score of " + assessment.getMaxScore());
        }

        // 6. Save Grade
        Grade grade = Grade.create(
                GradeId.generate(),
                command.assessmentId(),
                command.studentId(),
                command.value(),
                clock.instant()
        );

        gradeRepository.save(grade);

        return GradeResult.from(grade);
    }
}
