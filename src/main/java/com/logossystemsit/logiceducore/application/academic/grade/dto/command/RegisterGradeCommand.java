package com.logossystemsit.logiceducore.application.academic.grade.dto.command;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.math.BigDecimal;

public record RegisterGradeCommand(
        AssessmentId assessmentId,
        UserId studentId,
        BigDecimal value,
        UserId teacherId
) {}
