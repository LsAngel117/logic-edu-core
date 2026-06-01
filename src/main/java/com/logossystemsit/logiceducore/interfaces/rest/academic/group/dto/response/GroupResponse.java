package com.logossystemsit.logiceducore.interfaces.rest.academic.group.dto.response;

import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;

import java.util.List;

public record GroupResponse(
        String id,
        String schoolId,
        String subjectId,
        String academicPeriodId,
        String branchId,
        String teacherId,
        String code,
        int capacity,
        String status,
        Long version,
        List<ScheduleResponse> schedules,
        String createdAt,
        String updatedAt
) {
    public static GroupResponse from(GroupResult result) {
        List<ScheduleResponse> scheduleResponses = result.schedules().stream()
                .map(s -> new ScheduleResponse(
                        s.id(), s.dayOfWeek(),
                        s.startTime() != null ? s.startTime().toString() : null,
                        s.endTime() != null ? s.endTime().toString() : null,
                        s.classroom()))
                .toList();

        return new GroupResponse(
                result.id(),
                result.schoolId(),
                result.subjectId(),
                result.academicPeriodId(),
                result.branchId(),
                result.teacherId(),
                result.code(),
                result.capacity(),
                result.status(),
                result.version(),
                scheduleResponses,
                result.createdAt() != null ? result.createdAt().toString() : null,
                result.updatedAt() != null ? result.updatedAt().toString() : null
        );
    }

    public record ScheduleResponse(
            String id,
            String dayOfWeek,
            String startTime,
            String endTime,
            String classroom
    ) {}
}
