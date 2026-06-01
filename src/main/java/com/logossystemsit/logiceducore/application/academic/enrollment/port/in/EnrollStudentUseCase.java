package com.logossystemsit.logiceducore.application.academic.enrollment.port.in;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.command.EnrollStudentCommand;
import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;

public interface EnrollStudentUseCase {

    EnrollmentResult execute(EnrollStudentCommand command);
}
