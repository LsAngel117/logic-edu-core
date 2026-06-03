package com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentType;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.entity.AssessmentEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.repository.AssessmentJpaRepository;

import java.util.List;
import java.util.Optional;

public class AssessmentRepositoryAdapter implements AssessmentRepository {

    private final AssessmentJpaRepository jpa;

    public AssessmentRepositoryAdapter(AssessmentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Assessment assessment) {
        AssessmentEntity entity = mapToEntity(assessment);
        jpa.save(entity);
    }

    @Override
    public Optional<Assessment> findById(AssessmentId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<Assessment> findByGroupId(GroupId groupId) {
        return jpa.findByGroupId(groupId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public boolean existsByGroupIdAndName(GroupId groupId, String name) {
        return jpa.existsByGroupIdAndName(groupId.value(), name);
    }

    @Override
    public void delete(Assessment assessment) {
        jpa.delete(mapToEntity(assessment));
    }

    private AssessmentEntity mapToEntity(Assessment assessment) {
        AssessmentEntity entity = new AssessmentEntity();
        entity.setId(assessment.getId().value());
        entity.setGroupId(assessment.getGroupId().value());
        entity.setEvaluationPeriodId(
                assessment.getEvaluationPeriodId()
                        .map(EvaluationPeriodId::value)
                        .orElse(null)
        );
        entity.setName(assessment.getName());
        entity.setType(assessment.getType().name());
        entity.setWeight(assessment.getWeight());
        entity.setMaxScore(assessment.getMaxScore());
        entity.setCreatedAt(assessment.getCreatedAt());
        entity.setUpdatedAt(assessment.getUpdatedAt());
        return entity;
    }

    private Assessment mapToDomain(AssessmentEntity entity) {
        return Assessment.restore(
                new AssessmentId(entity.getId()),
                new GroupId(entity.getGroupId()),
                entity.getEvaluationPeriodId() != null
                        ? new EvaluationPeriodId(entity.getEvaluationPeriodId())
                        : null,
                entity.getName(),
                AssessmentType.valueOf(entity.getType()),
                entity.getWeight(),
                entity.getMaxScore(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
