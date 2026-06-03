package com.logossystemsit.logiceducore.application.academic.group.port.out;

import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {

    void save(Group group);

    Optional<Group> findById(GroupId id);

    List<Group> findBySchoolId(SchoolId schoolId);

    List<Group> findBySchoolIdAndBranchId(SchoolId schoolId, BranchId branchId);

    List<Group> findBySchoolIdAndPeriodId(SchoolId schoolId, AcademicPeriodId periodId);

    boolean existsBySchoolIdAndCode(SchoolId schoolId, String code);
}
