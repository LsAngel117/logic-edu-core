package com.logossystemsit.logiceducore.interfaces.rest.academic.evaluation.dto.request;

public record CreateEvaluationPeriodRequest(
        String name,
        int sequence,
        String weight,
        String startDate,
        String endDate
) {}
