package com.logossystemsit.logiceducore.application.academic.group.usecase;

import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.ListGroupsBySchoolUseCase;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListGroupsBySchoolService implements ListGroupsBySchoolUseCase {

    private final GroupRepository groupRepository;

    public ListGroupsBySchoolService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResult> execute(SchoolId schoolId, BranchId branchId, AcademicPeriodId periodId) {
        List<com.logossystemsit.logiceducore.domain.academic.group.model.Group> groups;

        if (branchId != null) {
            groups = groupRepository.findBySchoolIdAndBranchId(schoolId, branchId);
        } else if (periodId != null) {
            groups = groupRepository.findBySchoolIdAndPeriodId(schoolId, periodId);
        } else {
            groups = groupRepository.findBySchoolId(schoolId);
        }

        return groups.stream()
                .map(GroupResult::from)
                .toList();
    }
}
