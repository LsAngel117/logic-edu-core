package com.logossystemsit.logiceducore.application.academic.attendance.port.in;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.RegisterAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;

public interface RegisterAttendanceUseCase {
    AttendanceResult execute(RegisterAttendanceCommand command);
}
