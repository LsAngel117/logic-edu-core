package com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.Enrollment;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentId;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.entity.EnrollmentEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.repository.EnrollmentJpaRepository;

import java.util.List;
import java.util.Optional;

public class EnrollmentRepositoryAdapter implements EnrollmentRepository {

    private final EnrollmentJpaRepository jpa;

    public EnrollmentRepositoryAdapter(EnrollmentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Enrollment enrollment) {
        EnrollmentEntity entity = mapToEntity(enrollment);
        jpa.save(entity);
    }

    @Override
    public Optional<Enrollment> findById(EnrollmentId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<Enrollment> findByGroupId(GroupId groupId) {
        return jpa.findByGroupId(groupId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public long countActiveByGroupId(GroupId groupId) {
        return jpa.countByGroupIdAndStatus(groupId.value(), "ACTIVE");
    }

    @Override
    public boolean existsByUserIdAndGroupId(UserId userId, GroupId groupId) {
        return jpa.existsByUserIdAndGroupId(userId.value(), groupId.value());
    }

    @Override
    public boolean existsActiveByStudentAndSubjectAndPeriod(UserId userId, SubjectId subjectId, AcademicPeriodId periodId) {
        return jpa.existsActiveByStudentAndSubjectAndPeriod(
                userId.value(), subjectId.value(), periodId.value());
    }

    private EnrollmentEntity mapToEntity(Enrollment enrollment) {
        EnrollmentEntity entity = new EnrollmentEntity();
        entity.setId(enrollment.getId().value());
        entity.setUserId(enrollment.getUserId().value());
        entity.setGroupId(enrollment.getGroupId().value());
        entity.setStatus(enrollment.getStatus().name());
        entity.setEnrolledAt(enrollment.getEnrolledAt());
        entity.setUpdatedAt(enrollment.getUpdatedAt());
        return entity;
    }

    private Enrollment mapToDomain(EnrollmentEntity entity) {
        return Enrollment.restore(
                new EnrollmentId(entity.getId()),
                new UserId(entity.getUserId()),
                new GroupId(entity.getGroupId()),
                EnrollmentStatus.valueOf(entity.getStatus()),
                entity.getEnrolledAt(),
                entity.getUpdatedAt()
        );
    }
}
