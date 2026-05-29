package com.logossystemsit.logiceducore.application.academic.evaluation.dto.command;

import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateEvaluationPeriodCommand(
        EvaluationPeriodId evaluationPeriodId,
        AcademicPeriodId periodId,
        String name,
        Integer sequence,
        BigDecimal weight,
        LocalDate startDate,
        LocalDate endDate
) {}
