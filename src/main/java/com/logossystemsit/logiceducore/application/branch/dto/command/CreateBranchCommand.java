package com.logossystemsit.logiceducore.application.branch.dto.command;

import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

public record CreateBranchCommand(
        BranchId branchId,
        SchoolId schoolId,
        BranchName name,
        BranchCode code,
        BranchShortName shortName,
        BranchDescription description,
        BranchEmail email,
        BranchPhone phone,
        BranchAddress address,
        BranchType type
) {}
