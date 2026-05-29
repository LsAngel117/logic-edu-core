package com.logossystemsit.logiceducore.application.branch.port.out;

import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchName;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchType;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;
import java.util.Optional;

public interface BranchRepository {

    void save(Branch branch);

    Optional<Branch> findById(BranchId id);

    List<Branch> findBySchoolId(SchoolId schoolId);

    boolean existsBySchoolIdAndName(SchoolId schoolId, BranchName name);

    int countBySchoolIdAndType(SchoolId schoolId, BranchType type);

    int countActiveBySchoolId(SchoolId schoolId);

    boolean existsActiveBySchoolId(SchoolId schoolId);
}
