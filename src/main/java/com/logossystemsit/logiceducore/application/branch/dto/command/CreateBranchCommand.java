package com.logossystemsit.logiceducore.application.branch.dto.command;

import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.shared.valueobject.City;
import com.logossystemsit.logiceducore.shared.valueobject.Country;

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
        City city,
        Country country,
        BranchType type
) {}
