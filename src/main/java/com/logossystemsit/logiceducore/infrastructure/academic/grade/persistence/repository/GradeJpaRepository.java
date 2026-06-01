package com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.entity.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeJpaRepository extends JpaRepository<GradeEntity, String> {

    List<GradeEntity> findByAssessmentId(String assessmentId);

    Optional<GradeEntity> findByAssessmentIdAndStudentId(String assessmentId, String studentId);

    long countByAssessmentId(String assessmentId);
}
