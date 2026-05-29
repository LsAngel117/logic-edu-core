package com.logossystemsit.logiceducore.application.branch.port.in;

import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;

public interface ListBranchesBySchoolUseCase {

    List<BranchResult> execute(SchoolId schoolId);
}
