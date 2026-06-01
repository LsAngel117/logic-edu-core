package com.logossystemsit.logiceducore.application.academic.enrollment.usecase;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.command.EnrollStudentCommand;
import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.EnrollStudentUseCase;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.Enrollment;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentId;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupStatus;
import com.logossystemsit.logiceducore.domain.user.model.User;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class EnrollStudentService implements EnrollStudentUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public EnrollStudentService(
            EnrollmentRepository enrollmentRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            Clock clock
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public EnrollmentResult execute(EnrollStudentCommand command) {
        // 1. Load Group by groupId
        var group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Group not found: " + command.groupId().value()));

        // 2. Validate group is ACTIVE
        if (group.getStatus() != GroupStatus.ACTIVE) {
            throw new IllegalStateException("Group is inactive");
        }

        // 3. Validate student exists and is ACTIVE
        User student = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student not found: " + command.userId().value()));
        if (!student.isActive()) {
            throw new IllegalStateException("Student is not active");
        }

        // 4. Validate no duplicate enrollment
        if (enrollmentRepository.existsByUserIdAndGroupId(command.userId(), command.groupId())) {
            throw new IllegalStateException("Student already enrolled in this group");
        }

        // 5. Cross-aggregate: validate no same subject+period
        if (enrollmentRepository.existsActiveByStudentAndSubjectAndPeriod(
                command.userId(), group.getSubjectId(), group.getAcademicPeriodId())) {
            throw new IllegalStateException(
                    "Student already has an active enrollment for the same subject and period");
        }

        // 6. Capacity check
        long activeCount = enrollmentRepository.countActiveByGroupId(command.groupId());
        if (activeCount >= group.getCapacity()) {
            throw new IllegalStateException("Group has reached maximum capacity");
        }

        // 7. Create and save enrollment first
        Enrollment enrollment = Enrollment.create(
                EnrollmentId.generate(),
                command.userId(),
                command.groupId(),
                clock.instant()
        );
        enrollmentRepository.save(enrollment);

        // 8. Save Group to check optimistic lock
        try {
            groupRepository.save(group);
        } catch (OptimisticLockingFailureException e) {
            throw new OptimisticLockingFailureException("Group capacity changed, please retry");
        }

        return EnrollmentResult.from(enrollment);
    }
}
