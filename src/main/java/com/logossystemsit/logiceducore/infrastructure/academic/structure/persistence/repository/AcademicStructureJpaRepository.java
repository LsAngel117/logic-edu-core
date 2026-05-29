package com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.entity.AcademicStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicStructureJpaRepository extends JpaRepository<AcademicStructureEntity, String> {

    Optional<AcademicStructureEntity> findBySchoolId(String schoolId);

    Optional<AcademicStructureEntity> findBySchoolIdAndActiveTrue(String schoolId);

    Optional<AcademicStructureEntity> findTopBySchoolIdOrderByVersionDesc(String schoolId);
}
