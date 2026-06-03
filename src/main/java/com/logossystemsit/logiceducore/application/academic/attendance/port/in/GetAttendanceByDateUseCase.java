package com.logossystemsit.logiceducore.application.academic.attendance.port.in;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;

import java.time.LocalDate;
import java.util.List;

public interface GetAttendanceByDateUseCase {
    List<AttendanceResult> execute(GroupId groupId, LocalDate date);
}
