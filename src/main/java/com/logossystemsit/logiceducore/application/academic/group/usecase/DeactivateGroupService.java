package com.logossystemsit.logiceducore.application.academic.group.usecase;

import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.DeactivateGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeactivateGroupService implements DeactivateGroupUseCase {

    private final GroupRepository groupRepository;
    private final Clock clock;

    public DeactivateGroupService(GroupRepository groupRepository, Clock clock) {
        this.groupRepository = groupRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public GroupResult execute(GroupId id) {
        var group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + id.value()));

        var deactivated = group.deactivate(clock.instant());
        groupRepository.save(deactivated);

        return GroupResult.from(deactivated);
    }
}
