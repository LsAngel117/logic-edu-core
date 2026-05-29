package com.logossystemsit.logiceducore.application.academic.period.dto.command;

import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.PeriodType;

import java.time.LocalDate;

public record UpdateAcademicPeriodCommand(
        AcademicPeriodId periodId,
        AcademicLevelId levelId,
        String name,
        Integer sequence,
        PeriodType periodType,
        LocalDate startDate,
        LocalDate endDate
) {}
