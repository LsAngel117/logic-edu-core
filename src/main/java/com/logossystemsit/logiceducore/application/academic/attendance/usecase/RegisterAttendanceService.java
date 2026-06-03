package com.logossystemsit.logiceducore.application.academic.attendance.usecase;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.RegisterAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.application.academic.attendance.port.in.RegisterAttendanceUseCase;
import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.Attendance;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject.AttendanceId;
import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class RegisterAttendanceService implements RegisterAttendanceUseCase {

    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final Clock clock;

    public RegisterAttendanceService(
            AttendanceRepository attendanceRepository,
            GroupRepository groupRepository,
            Clock clock
    ) {
        this.attendanceRepository = attendanceRepository;
        this.groupRepository = groupRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AttendanceResult execute(RegisterAttendanceCommand command) {
        Group group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Group not found: " + command.groupId().value()));

        if (group.getStatus() != GroupStatus.ACTIVE) {
            throw new IllegalStateException("Cannot register attendance: group is not active");
        }

        if (!group.getTeacherId().equals(command.teacherId())) {
            throw new IllegalStateException("Teacher is not authorized for this group");
        }

        Attendance attendance = Attendance.create(
                AttendanceId.generate(),
                command.groupId(),
                command.studentId(),
                command.date(),
                command.status(),
                command.observations(),
                clock.instant()
        );

        attendanceRepository.save(attendance);

        return AttendanceResult.from(attendance);
    }
}
