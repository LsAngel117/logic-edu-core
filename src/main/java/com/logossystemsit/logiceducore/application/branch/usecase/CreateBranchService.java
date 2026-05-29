package com.logossystemsit.logiceducore.application.branch.usecase;

import com.logossystemsit.logiceducore.application.branch.dto.command.CreateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.CreateBranchUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchType;
import com.logossystemsit.logiceducore.domain.school.model.School;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

public class CreateBranchService implements CreateBranchUseCase {

    private final BranchRepository branchRepository;
    private final SchoolRepository schoolRepository;
    private final Clock clock;

    public CreateBranchService(
            BranchRepository branchRepository,
            SchoolRepository schoolRepository,
            Clock clock) {
        this.branchRepository = branchRepository;
        this.schoolRepository = schoolRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public BranchResult execute(CreateBranchCommand command) {
        School school = schoolRepository.findById(command.schoolId())
                .orElseThrow(() -> new IllegalArgumentException("School not found"));

        if (!school.isActive()) {
            throw new IllegalStateException("Cannot create branch in an inactive school");
        }

        if (command.type() == BranchType.MAIN
                && branchRepository.countBySchoolIdAndType(command.schoolId(), BranchType.MAIN) > 0) {
            throw new IllegalArgumentException("A MAIN branch already exists for this school");
        }

        if (branchRepository.existsBySchoolIdAndName(command.schoolId(), command.name())) {
            throw new IllegalArgumentException("Branch name already exists within this school");
        }

        Instant now = clock.instant();

        Branch branch = Branch.create(
                command.branchId(),
                command.schoolId(),
                command.name(),
                command.code(),
                command.shortName(),
                command.description(),
                command.email(),
                command.phone(),
                command.address(),
                command.type(),
                now
        );

        branchRepository.save(branch);

        return BranchResult.from(branch);
    }
}
