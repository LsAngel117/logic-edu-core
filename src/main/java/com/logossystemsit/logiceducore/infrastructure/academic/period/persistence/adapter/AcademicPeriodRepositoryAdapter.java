package com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.*;
import com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.entity.AcademicPeriodEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.repository.AcademicPeriodJpaRepository;

import java.util.List;
import java.util.Optional;

public class AcademicPeriodRepositoryAdapter implements AcademicPeriodRepository {

    private final AcademicPeriodJpaRepository jpa;

    public AcademicPeriodRepositoryAdapter(AcademicPeriodJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(AcademicPeriod period) {
        AcademicPeriodEntity entity = mapToEntity(period);
        jpa.save(entity);
    }

    @Override
    public Optional<AcademicPeriod> findById(AcademicPeriodId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<AcademicPeriod> findByLevelId(AcademicLevelId levelId) {
        return jpa.findByLevelId(levelId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public boolean existsActiveEvaluationPeriodsByPeriodId(AcademicPeriodId periodId) {
        return jpa.existsActiveEvaluationPeriodsByPeriodId(periodId.value());
    }

    private AcademicPeriodEntity mapToEntity(AcademicPeriod period) {
        AcademicPeriodEntity e = new AcademicPeriodEntity();
        e.setId(period.getId().value());
        e.setLevelId(period.getLevelId().value());
        e.setPeriodType(period.getPeriodType().name());
        e.setName(period.getName());
        e.setSequence(period.getSequence());
        e.setStartDate(period.getStartDate());
        e.setEndDate(period.getEndDate());
        e.setStatus(period.getStatus().name());
        e.setCreatedAt(period.getCreatedAt());
        e.setUpdatedAt(period.getUpdatedAt());
        return e;
    }

    private AcademicPeriod mapToDomain(AcademicPeriodEntity e) {
        return AcademicPeriod.restore(
                new AcademicPeriodId(e.getId()),
                new AcademicLevelId(e.getLevelId()),
                PeriodType.valueOf(e.getPeriodType()),
                e.getName(),
                e.getSequence(),
                e.getStartDate(),
                e.getEndDate(),
                PeriodStatus.valueOf(e.getStatus()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
