package com.logossystemsit.logiceducore.application.academic.attendance.usecase;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.UpdateAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.application.academic.attendance.port.in.UpdateAttendanceUseCase;
import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.Attendance;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GROUP_NOT_FOUND,
                        "Group not found: " + command.groupId().value()));

        if (!group.getTeacherId().equals(command.teacherId())) {
            throw new BusinessRuleException(ErrorCode.TEACHER_NOT_ASSIGNED, "Teacher is not authorized for this group");
        }

        Attendance attendance = attendanceRepository
                .findByGroupIdAndDateAndStudentId(
                        command.groupId(), command.date(), command.studentId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ATTENDANCE_NOT_FOUND,
                        "Attendance not found"));

        Attendance updated = attendance.changeStatus(
                command.status(), command.observations(), clock.instant()
        );

        attendanceRepository.save(updated);

        return AttendanceResult.from(updated);
    }
}
