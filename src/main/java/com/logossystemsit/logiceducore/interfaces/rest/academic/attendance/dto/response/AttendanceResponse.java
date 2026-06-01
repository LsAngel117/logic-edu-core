package com.logossystemsit.logiceducore.interfaces.rest.academic.attendance.dto.response;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;

import java.time.Instant;
import java.time.LocalDate;

public record AttendanceResponse(
        String id,
        String groupId,
        String studentId,
        LocalDate date,
        String status,
        String observations,
        Instant createdAt,
        Instant updatedAt
) {
    public static AttendanceResponse from(AttendanceResult result) {
        return new AttendanceResponse(
                result.id(),
                result.groupId(),
                result.studentId(),
                result.date(),
                result.status(),
                result.observations(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
