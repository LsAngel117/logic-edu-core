package com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectJpaRepository extends JpaRepository<SubjectEntity, String> {

    List<SubjectEntity> findBySchoolId(String schoolId);

    boolean existsBySchoolIdAndCode(String schoolId, String code);
}
