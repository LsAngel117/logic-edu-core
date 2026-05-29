package com.logossystemsit.logiceducore.infrastructure.branch.persistence.repository;

import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchType;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.entity.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BranchJpaRepository extends JpaRepository<BranchEntity, String> {

    List<BranchEntity> findBySchoolId(String schoolId);

    boolean existsBySchoolIdAndName(String schoolId, String name);

    int countBySchoolIdAndType(String schoolId, BranchType type);

    @Query("SELECT COUNT(b) FROM BranchEntity b WHERE b.schoolId = :schoolId AND b.status = :status")
    int countActiveBySchoolId(@Param("schoolId") String schoolId, @Param("status") Branch.Status status);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BranchEntity b WHERE b.schoolId = :schoolId AND b.status = :status")
    boolean existsActiveBySchoolId(@Param("schoolId") String schoolId, @Param("status") Branch.Status status);
}
