package com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentEntity, String> {

    List<EnrollmentEntity> findByGroupId(String groupId);

    long countByGroupIdAndStatus(String groupId, String status);

    boolean existsByUserIdAndGroupId(String userId, String groupId);

    @Query("SELECT COUNT(e) > 0 FROM EnrollmentEntity e " +
           "JOIN com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity.GroupEntity g ON e.groupId = g.id " +
           "WHERE e.userId = :userId AND g.subjectId = :subjectId AND g.academicPeriodId = :periodId AND e.status = 'ACTIVE'")
    boolean existsActiveByStudentAndSubjectAndPeriod(@Param("userId") String userId,
                                                     @Param("subjectId") String subjectId,
                                                     @Param("periodId") String periodId);
}
