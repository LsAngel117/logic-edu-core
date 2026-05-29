package com.logossystemsit.logiceducore.application.branch.usecase;

import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.DeactivateBranchUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeactivateBranchService implements DeactivateBranchUseCase {

    private final BranchRepository branchRepository;
    private final Clock clock;

    public DeactivateBranchService(BranchRepository branchRepository, Clock clock) {
        this.branchRepository = branchRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public BranchResult execute(SchoolId schoolId, BranchId branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        if (!branch.getSchoolId().equals(schoolId)) {
            throw new IllegalArgumentException("Branch not found");
        }

        if (!branch.isActive()) {
            throw new IllegalStateException("Branch is already inactive");
        }

        // If MAIN branch, check for active secondary branches
        if (branch.isMain()) {
            int activeBranches = branchRepository.countActiveBySchoolId(schoolId);
            if (activeBranches > 1) {
                throw new IllegalStateException(
                        "Cannot deactivate the main branch while there are active secondary branches");
            }
        }

        Branch deactivated = branch.deactivate(clock.instant());
        branchRepository.save(deactivated);

        return BranchResult.from(deactivated);
    }
}
