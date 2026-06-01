package com.logossystemsit.logiceducore.application.academic.enrollment.usecase;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.GetEnrollmentUseCase;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentId;
import org.springframework.transaction.annotation.Transactional;

public class GetEnrollmentService implements GetEnrollmentUseCase {

    private final EnrollmentRepository enrollmentRepository;

    public GetEnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResult execute(EnrollmentId id) {
        var enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + id.value()));
        return EnrollmentResult.from(enrollment);
    }
}
