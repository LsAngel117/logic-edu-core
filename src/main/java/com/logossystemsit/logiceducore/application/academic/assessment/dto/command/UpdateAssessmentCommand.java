package com.logossystemsit.logiceducore.application.academic.assessment.dto.command;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentType;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.math.BigDecimal;

public record UpdateAssessmentCommand(
        AssessmentId assessmentId,
        GroupId groupId,
        String name,
        AssessmentType type,
        BigDecimal weight,
        BigDecimal maxScore,
        EvaluationPeriodId evaluationPeriodId,
        UserId teacherId
) {}
