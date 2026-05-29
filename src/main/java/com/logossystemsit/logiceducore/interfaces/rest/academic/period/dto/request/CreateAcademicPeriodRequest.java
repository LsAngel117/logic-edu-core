package com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.request;

public record CreateAcademicPeriodRequest(
        String periodType,
        String name,
        int sequence,
        String startDate,
        String endDate
) {}
