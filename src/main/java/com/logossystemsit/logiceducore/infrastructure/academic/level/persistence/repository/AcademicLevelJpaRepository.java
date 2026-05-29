package com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.entity.AcademicLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicLevelJpaRepository extends JpaRepository<AcademicLevelEntity, String> {

    List<AcademicLevelEntity> findAllBySchoolId(String schoolId);

    boolean existsBySchoolIdAndNumber(String schoolId, int number);

    default boolean existsActivePeriodsByLevelId(String levelId) {
        // TODO: query academic_periods table when available
        return false;
    }
}
