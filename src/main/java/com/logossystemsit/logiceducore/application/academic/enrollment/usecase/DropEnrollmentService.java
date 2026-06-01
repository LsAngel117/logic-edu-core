package com.logossystemsit.logiceducore.application.academic.enrollment.usecase;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.DropEnrollmentUseCase;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DropEnrollmentService implements DropEnrollmentUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final Clock clock;

    public DropEnrollmentService(EnrollmentRepository enrollmentRepository, Clock clock) {
        this.enrollmentRepository = enrollmentRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public EnrollmentResult execute(EnrollmentId id) {
        var enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + id.value()));

        var dropped = enrollment.drop(clock.instant());

        if (dropped != enrollment) {
            enrollmentRepository.save(dropped);
        }

        return EnrollmentResult.from(dropped);
    }
}
