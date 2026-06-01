package com.logossystemsit.logiceducore.interfaces.rest.academic.grade.dto.request;

import java.math.BigDecimal;

public record RegisterGradeRequest(
        String studentId,
        BigDecimal value
) {}
