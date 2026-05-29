package com.logossystemsit.logiceducore.application.branch.usecase;

import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.ListBranchesBySchoolUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListBranchesBySchoolService implements ListBranchesBySchoolUseCase {

    private final BranchRepository branchRepository;

    public ListBranchesBySchoolService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResult> execute(SchoolId schoolId) {
        return branchRepository.findBySchoolId(schoolId)
                .stream()
                .map(BranchResult::from)
                .toList();
    }
}
