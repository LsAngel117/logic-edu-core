package com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.entity.EvaluationPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EvaluationPeriodJpaRepository extends JpaRepository<EvaluationPeriodEntity, String> {

    List<EvaluationPeriodEntity> findByPeriodId(String periodId);

    @Query("SELECT COALESCE(SUM(e.weight), 0) FROM EvaluationPeriodEntity e WHERE e.periodId = :periodId")
    BigDecimal sumWeightsByPeriodId(@Param("periodId") String periodId);

    boolean existsByPeriodIdAndStatus(String periodId, String status);
}
