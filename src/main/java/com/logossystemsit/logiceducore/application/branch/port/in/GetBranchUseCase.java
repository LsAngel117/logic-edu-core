package com.logossystemsit.logiceducore.application.branch.port.in;

import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

public interface GetBranchUseCase {

    BranchResult execute(SchoolId schoolId, BranchId branchId);
}
