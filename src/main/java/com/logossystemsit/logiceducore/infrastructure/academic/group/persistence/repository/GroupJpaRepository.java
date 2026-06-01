package com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupJpaRepository extends JpaRepository<GroupEntity, String> {

    List<GroupEntity> findBySchoolId(String schoolId);

    List<GroupEntity> findBySchoolIdAndBranchId(String schoolId, String branchId);

    List<GroupEntity> findBySchoolIdAndAcademicPeriodId(String schoolId, String academicPeriodId);

    boolean existsBySchoolIdAndCode(String schoolId, String code);
}
