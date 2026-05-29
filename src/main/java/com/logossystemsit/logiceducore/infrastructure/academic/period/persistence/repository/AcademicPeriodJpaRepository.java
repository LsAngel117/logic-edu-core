package com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.entity.AcademicPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicPeriodJpaRepository extends JpaRepository<AcademicPeriodEntity, String> {

    List<AcademicPeriodEntity> findByLevelId(String levelId);

    default boolean existsActiveEvaluationPeriodsByPeriodId(String periodId) {
        // TODO: query evaluation_periods table when available
        return false;
    }
}
