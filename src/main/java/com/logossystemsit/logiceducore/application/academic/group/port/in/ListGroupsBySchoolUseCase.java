package com.logossystemsit.logiceducore.application.academic.group.port.in;

import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;

public interface ListGroupsBySchoolUseCase {

    List<GroupResult> execute(SchoolId schoolId, BranchId branchId, AcademicPeriodId periodId);
}
