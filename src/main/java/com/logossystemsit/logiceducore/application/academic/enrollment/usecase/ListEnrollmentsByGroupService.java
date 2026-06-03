package com.logossystemsit.logiceducore.application.academic.enrollment.usecase;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.ListEnrollmentsByGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListEnrollmentsByGroupService implements ListEnrollmentsByGroupUseCase {

    private final EnrollmentRepository enrollmentRepository;

    public ListEnrollmentsByGroupService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResult> execute(GroupId groupId) {
        return enrollmentRepository.findByGroupId(groupId).stream()
                .map(EnrollmentResult::from)
                .toList();
    }
}
