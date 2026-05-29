package com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.entity.EvaluationPeriodEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.repository.EvaluationPeriodJpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class EvaluationPeriodRepositoryAdapter implements EvaluationPeriodRepository {

    private final EvaluationPeriodJpaRepository jpa;

    public EvaluationPeriodRepositoryAdapter(EvaluationPeriodJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(EvaluationPeriod period) {
        EvaluationPeriodEntity entity = mapToEntity(period);
        jpa.save(entity);
    }

    @Override
    public Optional<EvaluationPeriod> findById(EvaluationPeriodId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<EvaluationPeriod> findByPeriodId(AcademicPeriodId periodId) {
        return jpa.findByPeriodId(periodId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public BigDecimal sumWeightsByPeriodId(AcademicPeriodId periodId) {
        BigDecimal sum = jpa.sumWeightsByPeriodId(periodId.value());
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private EvaluationPeriodEntity mapToEntity(EvaluationPeriod period) {
        EvaluationPeriodEntity e = new EvaluationPeriodEntity();
        e.setId(period.getId().value());
        e.setPeriodId(period.getPeriodId().value());
        e.setName(period.getName());
        e.setSequence(period.getSequence());
        e.setWeight(period.getWeight());
        e.setStartDate(period.getStartDate());
        e.setEndDate(period.getEndDate());
        e.setStatus(period.getStatus().name());
        e.setCreatedAt(period.getCreatedAt());
        e.setUpdatedAt(period.getUpdatedAt());
        return e;
    }

    private EvaluationPeriod mapToDomain(EvaluationPeriodEntity e) {
        return EvaluationPeriod.restore(
                new EvaluationPeriodId(e.getId()),
                new AcademicPeriodId(e.getPeriodId()),
                e.getName(),
                e.getSequence(),
                e.getWeight(),
                e.getStartDate(),
                e.getEndDate(),
                EvaluationPeriodStatus.valueOf(e.getStatus()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
