package com.logossystemsit.logiceducore.application.academic.group.usecase;

import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.GetGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class GetGroupService implements GetGroupUseCase {

    private final GroupRepository groupRepository;

    public GetGroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResult execute(GroupId id) {
        var group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GROUP_NOT_FOUND, "Group not found: " + id.value()));
        return GroupResult.from(group);
    }
}
