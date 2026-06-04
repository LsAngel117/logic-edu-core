package com.logossystemsit.logiceducore.application.academic.group.usecase;

import com.logossystemsit.logiceducore.application.academic.group.dto.command.CreateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.CreateGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.*;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.group.model.Schedule;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.ScheduleId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectStatus;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SCHOOL_NOT_FOUND,
                        "School not found: " + command.schoolId().value()));
        if (!school.isActive()) {
            throw new BusinessRuleException(ErrorCode.SCHOOL_INACTIVE, "School is not active");
        }

        var subject = subjectRepository.findById(command.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SUBJECT_NOT_FOUND,
                        "Subject not found: " + command.subjectId().value()));
        if (subject.getStatus() != SubjectStatus.ACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Subject is not active");
        }

        var period = periodRepository.findById(command.academicPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ACADEMIC_PERIOD_NOT_FOUND,
                        "Academic period not found: " + command.academicPeriodId().value()));
        if (period.getStatus() != PeriodStatus.ACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Academic period is not active");
        }

        Branch branch = branchRepository.findById(command.branchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND,
                        "Branch not found: " + command.branchId().value()));
        if (!branch.isActive()) {
            throw new BusinessRuleException(ErrorCode.BRANCH_INACTIVE, "Branch is not active");
        }

        var memberships = membershipRepository.findByUserId(command.teacherId());
        boolean hasTeacherRole = memberships.stream().anyMatch(m -> m.isActive() && m.isTeacher());
        if (!hasTeacherRole) {
            throw new BusinessRuleException(ErrorCode.TEACHER_NOT_ASSIGNED,
                    "Teacher does not have TEACHER role: " + command.teacherId().value());
        }

        if (groupRepository.existsBySchoolIdAndCode(command.schoolId(), command.code())) {
            throw new BusinessRuleException(ErrorCode.GROUP_ALREADY_EXISTS,
                    "Group code " + command.code() + " already exists");
        }

        if (command.capacity() <= 0) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "capacity must be greater than zero");
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
