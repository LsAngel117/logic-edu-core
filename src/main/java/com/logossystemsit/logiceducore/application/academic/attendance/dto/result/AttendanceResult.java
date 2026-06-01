package com.logossystemsit.logiceducore.application.academic.attendance.dto.result;

import com.logossystemsit.logiceducore.domain.academic.attendance.model.Attendance;

import java.time.Instant;
import java.time.LocalDate;

public record AttendanceResult(
        String id,
        String groupId,
        String studentId,
        LocalDate date,
        String status,
        String observations,
        Instant createdAt,
        Instant updatedAt
) {
    public static AttendanceResult from(Attendance attendance) {
        return new AttendanceResult(
                attendance.getId().value(),
                attendance.getGroupId().value(),
                attendance.getStudentId().value(),
                attendance.getDate(),
                attendance.getStatus().name(),
                attendance.getObservations(),
                attendance.getCreatedAt(),
                attendance.getUpdatedAt()
        );
    }
}
