package com.logossystemsit.logiceducore.application.branch.usecase;

import com.logossystemsit.logiceducore.application.branch.dto.command.UpdateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.UpdateBranchUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchType;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

public class UpdateBranchService implements UpdateBranchUseCase {

    private final BranchRepository branchRepository;
    private final Clock clock;

    public UpdateBranchService(BranchRepository branchRepository, Clock clock) {
        this.branchRepository = branchRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public BranchResult execute(UpdateBranchCommand command) {
        Branch branch = branchRepository.findById(command.branchId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRANCH_NOT_FOUND, "Branch not found"));

        if (!branch.isActive()) {
            throw new BusinessRuleException(ErrorCode.BRANCH_INACTIVE, "Cannot modify an inactive branch");
        }

        // If type is changing to MAIN, check uniqueness (excluding self)
        if (command.type() == BranchType.MAIN && !branch.isMain()) {
            if (branchRepository.countBySchoolIdAndType(branch.getSchoolId(), BranchType.MAIN) > 0) {
                throw new BusinessRuleException(ErrorCode.BRANCH_MAIN_ALREADY_EXISTS, "A MAIN branch already exists for this school");
            }
        }

        // Validate name uniqueness if name changed
        if (!command.name().equals(branch.getName())
                && branchRepository.existsBySchoolIdAndName(branch.getSchoolId(), command.name())) {
            throw new BusinessRuleException(ErrorCode.BRANCH_ALREADY_EXISTS, "Branch name already exists within this school");
        }

        Instant now = clock.instant();

        Branch updated = branch.changeBasicInfo(
                command.name(),
                command.code(),
                command.shortName(),
                now
        );

        updated = updated.changeContactInfo(
                command.description(),
                command.email(),
                command.phone(),
                now
        );

        updated = updated.changeLocation(
                command.address(),
                now
        );

        if (!command.type().equals(branch.getType())) {
            updated = updated.changeType(command.type(), now);
        }

        branchRepository.save(updated);

        return BranchResult.from(updated);
    }
}
