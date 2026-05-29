package com.logossystemsit.logiceducore.interfaces.rest.academic.period.dto.request;

public record UpdateAcademicPeriodRequest(
        String name,
        String sequence,
        String periodType,
        String startDate,
        String endDate
) {}
