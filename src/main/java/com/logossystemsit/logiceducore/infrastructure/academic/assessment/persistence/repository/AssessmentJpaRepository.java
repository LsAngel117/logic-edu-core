package com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.entity.AssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentEntity, String> {

    List<AssessmentEntity> findByGroupId(String groupId);

    boolean existsByGroupIdAndName(String groupId, String name);
}
