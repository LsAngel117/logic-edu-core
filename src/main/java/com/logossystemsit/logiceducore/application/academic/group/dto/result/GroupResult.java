package com.logossystemsit.logiceducore.application.academic.group.dto.result;

import com.logossystemsit.logiceducore.domain.academic.group.model.Group;

import java.time.Instant;
import java.util.List;

public record GroupResult(
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
        List<ScheduleResult> schedules,
        Instant createdAt,
        Instant updatedAt
) {
    public static GroupResult from(Group group) {
        List<ScheduleResult> scheduleResults = group.getSchedules().stream()
                .map(s -> new ScheduleResult(
                        s.scheduleId().value(),
                        s.dayOfWeek(),
                        s.startTime(),
                        s.endTime(),
                        s.classroom()))
                .toList();

        return new GroupResult(
                group.getId().value(),
                group.getSchoolId().value(),
                group.getSubjectId().value(),
                group.getAcademicPeriodId().value(),
                group.getBranchId().value(),
                group.getTeacherId().value(),
                group.getCode(),
                group.getCapacity(),
                group.getStatus().name(),
                group.getVersion(),
                scheduleResults,
                group.getCreatedAt(),
                group.getUpdatedAt()
        );
    }

    public record ScheduleResult(
            String id,
            String dayOfWeek,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime,
            String classroom
    ) {}
}
