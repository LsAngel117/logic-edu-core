package com.logossystemsit.logiceducore.infrastructure.school.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.school.persistence.entity.SchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolJpaRepository extends JpaRepository<SchoolEntity, String> {

    Optional<SchoolEntity> findByName(String name);

    Optional<SchoolEntity> findByCode(String code);

    boolean existsByName(String name);

    boolean existsByCode(String code);
}
