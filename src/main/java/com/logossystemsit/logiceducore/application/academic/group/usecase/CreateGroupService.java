package com.logossystemsit.logiceducore.application.academic.group.usecase;

import com.logossystemsit.logiceducore.application.academic.group.dto.command.CreateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.command.ScheduleData;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.CreateGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.*;
import com.logossystemsit.logiceducore.domain.academic.period.model.PeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectStatus;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.school.model.School;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

public class CreateGroupService implements CreateGroupUseCase {

    private final GroupRepository groupRepository;
    private final SchoolRepository schoolRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicPeriodRepository periodRepository;
    private final BranchRepository branchRepository;
    private final MembershipRepository membershipRepository;
    private final Clock clock;

    public CreateGroupService(
            GroupRepository groupRepository,
            SchoolRepository schoolRepository,
            SubjectRepository subjectRepository,
            AcademicPeriodRepository periodRepository,
            BranchRepository branchRepository,
            MembershipRepository membershipRepository,
            Clock clock
    ) {
        this.groupRepository = groupRepository;
        this.schoolRepository = schoolRepository;
        this.subjectRepository = subjectRepository;
        this.periodRepository = periodRepository;
        this.branchRepository = branchRepository;
        this.membershipRepository = membershipRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public GroupResult execute(CreateGroupCommand command) {
        School school = schoolRepository.findById(command.schoolId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "School not found: " + command.schoolId().value()));
        if (!school.isActive()) {
            throw new IllegalStateException("School is not active");
        }

        var subject = subjectRepository.findById(command.subjectId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject not found: " + command.subjectId().value()));
        if (subject.getStatus() != SubjectStatus.ACTIVE) {
            throw new IllegalStateException("Subject is not active");
        }

        var period = periodRepository.findById(command.academicPeriodId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Academic period not found: " + command.academicPeriodId().value()));
        if (period.getStatus() != PeriodStatus.ACTIVE) {
            throw new IllegalStateException("Academic period is not active");
        }

        Branch branch = branchRepository.findById(command.branchId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Branch not found: " + command.branchId().value()));
        if (!branch.isActive()) {
            throw new IllegalStateException("Branch is not active");
        }

        var memberships = membershipRepository.findByUserId(command.teacherId());
        boolean hasTeacherRole = memberships.stream().anyMatch(m -> m.isActive() && m.isTeacher());
        if (!hasTeacherRole) {
            throw new IllegalStateException(
                    "Teacher does not have TEACHER role: " + command.teacherId().value());
        }

        if (groupRepository.existsBySchoolIdAndCode(command.schoolId(), command.code())) {
            throw new IllegalArgumentException(
                    "Group code " + command.code() + " already exists");
        }

        if (command.capacity() <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }

        List<Schedule> schedules = command.schedules().stream()
                .map(sd -> new Schedule(
                        ScheduleId.generate(),
                        sd.dayOfWeek(),
                        sd.startTime(),
                        sd.endTime(),
                        sd.classroom()))
                .toList();

        Group group = Group.create(
                GroupId.generate(),
                command.schoolId(),
                command.subjectId(),
                command.academicPeriodId(),
                command.branchId(),
                command.teacherId(),
                command.code(),
                command.capacity(),
                schedules,
                clock.instant()
        );

        groupRepository.save(group);

        return GroupResult.from(group);
    }
}
