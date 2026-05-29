package com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.request;

public record UpdateEvaluationPeriodRequest(
        String name,
        String sequence,
        String weight,
        String startDate,
        String endDate
) {}
