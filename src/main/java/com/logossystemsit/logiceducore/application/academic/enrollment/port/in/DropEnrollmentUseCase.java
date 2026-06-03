package com.logossystemsit.logiceducore.application.academic.enrollment.port.in;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.valueobject.EnrollmentId;

public interface DropEnrollmentUseCase {

    EnrollmentResult execute(EnrollmentId id);
}
