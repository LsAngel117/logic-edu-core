package com.logossystemsit.logiceducore.application.academic.period.dto.result;

import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;

import java.time.Instant;
import java.time.LocalDate;

public record AcademicPeriodResult(
        String id,
        String levelId,
        String periodType,
        String name,
        int sequence,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static AcademicPeriodResult from(AcademicPeriod period) {
        return new AcademicPeriodResult(
                period.getId().value(),
                period.getLevelId().value(),
                period.getPeriodType().name(),
                period.getName(),
                period.getSequence(),
                period.getStartDate(),
                period.getEndDate(),
                period.getStatus().name(),
                period.getCreatedAt(),
                period.getUpdatedAt()
        );
    }
}
