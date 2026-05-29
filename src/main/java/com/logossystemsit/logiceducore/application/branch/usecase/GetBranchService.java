package com.logossystemsit.logiceducore.application.branch.usecase;

import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.GetBranchUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.springframework.transaction.annotation.Transactional;

public class GetBranchService implements GetBranchUseCase {

    private final BranchRepository branchRepository;

    public GetBranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResult execute(SchoolId schoolId, BranchId branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        if (!branch.getSchoolId().equals(schoolId)) {
            throw new IllegalArgumentException("Branch not found");
        }

        return BranchResult.from(branch);
    }
}
