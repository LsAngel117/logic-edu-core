package com.logossystemsit.logiceducore.application.academic.attendance.port.in;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.UpdateAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;

public interface UpdateAttendanceUseCase {
    AttendanceResult execute(UpdateAttendanceCommand command);
}
