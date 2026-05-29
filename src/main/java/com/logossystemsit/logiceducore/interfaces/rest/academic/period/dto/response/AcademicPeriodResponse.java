package com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.response;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;

import java.time.Instant;
import java.time.LocalDate;

public record AcademicPeriodResponse(
        String id,
        String levelId,
        String periodType,
        String name,
        int sequence,
        String startDate,
        String endDate,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static AcademicPeriodResponse from(AcademicPeriodResult result) {
        return new AcademicPeriodResponse(
                result.id(),
                result.levelId(),
                result.periodType(),
                result.name(),
                result.sequence(),
                result.startDate() != null ? result.startDate().toString() : null,
                result.endDate() != null ? result.endDate().toString() : null,
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
