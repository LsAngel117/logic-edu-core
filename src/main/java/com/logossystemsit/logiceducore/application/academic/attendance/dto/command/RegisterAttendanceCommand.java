package com.logossystemsit.logiceducore.application.academic.attendance.dto.command;

import com.logossystemsit.logiceducore.domain.academic.attendance.model.AttendanceStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.time.LocalDate;

public record RegisterAttendanceCommand(
        GroupId groupId,
        LocalDate date,
        UserId studentId,
        AttendanceStatus status,
        String observations,
        UserId teacherId
) {}
