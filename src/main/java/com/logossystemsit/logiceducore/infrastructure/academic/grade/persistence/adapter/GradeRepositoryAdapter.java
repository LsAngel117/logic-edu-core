package com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.entity.GradeEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.repository.GradeJpaRepository;

import java.util.List;
import java.util.Optional;

public class GradeRepositoryAdapter implements GradeRepository {

    private final GradeJpaRepository jpa;

    public GradeRepositoryAdapter(GradeJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Grade grade) {
        jpa.save(mapToEntity(grade));
    }

    @Override
    public Optional<Grade> findById(GradeId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<Grade> findByAssessmentId(AssessmentId assessmentId) {
        return jpa.findByAssessmentId(assessmentId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public Optional<Grade> findByAssessmentIdAndStudentId(AssessmentId assessmentId, UserId studentId) {
        return jpa.findByAssessmentIdAndStudentId(assessmentId.value(), studentId.value())
                .map(this::mapToDomain);
    }

    @Override
    public long countByAssessmentId(AssessmentId assessmentId) {
        return jpa.countByAssessmentId(assessmentId.value());
    }

    private GradeEntity mapToEntity(Grade grade) {
        GradeEntity entity = new GradeEntity();
        entity.setId(grade.getId().value());
        entity.setAssessmentId(grade.getAssessmentId().value());
        entity.setStudentId(grade.getStudentId().value());
        entity.setValue(grade.getValue());
        entity.setGradedAt(grade.getGradedAt());
        entity.setUpdatedAt(grade.getUpdatedAt());
        return entity;
    }

    private Grade mapToDomain(GradeEntity entity) {
        return Grade.restore(
                new GradeId(entity.getId()),
                new AssessmentId(entity.getAssessmentId()),
                new UserId(entity.getStudentId()),
                entity.getValue(),
                entity.getGradedAt(),
                entity.getUpdatedAt()
        );
    }
}
