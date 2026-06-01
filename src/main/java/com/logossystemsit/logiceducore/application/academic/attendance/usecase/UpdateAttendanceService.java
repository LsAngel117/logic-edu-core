package com.logossystemsit.logiceducore.application.academic.attendance.usecase;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.UpdateAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.application.academic.attendance.port.in.UpdateAttendanceUseCase;
import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.Attendance;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class UpdateAttendanceService implements UpdateAttendanceUseCase {

    private final AttendanceRepository attendanceRepository;
    private final GroupRepository groupRepository;
    private final Clock clock;

    public UpdateAttendanceService(
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
    public AttendanceResult execute(UpdateAttendanceCommand command) {
        var group = groupRepository.findById(command.groupId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Group not found: " + command.groupId().value()));

        if (!group.getTeacherId().equals(command.teacherId())) {
            throw new IllegalStateException("Teacher is not authorized for this group");
        }

        Attendance attendance = attendanceRepository
                .findByGroupIdAndDateAndStudentId(
                        command.groupId(), command.date(), command.studentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Attendance not found"));

        Attendance updated = attendance.changeStatus(
                command.status(), command.observations(), clock.instant()
        );

        attendanceRepository.save(updated);

        return AttendanceResult.from(updated);
    }
}
