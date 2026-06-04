package com.logossystemsit.logiceducore.application.academic.group.usecase;

import com.logossystemsit.logiceducore.application.academic.group.dto.command.ScheduleData;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.UpdateGroupSchedulesUseCase;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.Schedule;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.ScheduleId;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

public class UpdateGroupSchedulesService implements UpdateGroupSchedulesUseCase {

    private final GroupRepository groupRepository;
    private final Clock clock;

    public UpdateGroupSchedulesService(GroupRepository groupRepository, Clock clock) {
        this.groupRepository = groupRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public GroupResult execute(GroupId groupId, List<ScheduleData> schedules) {
        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GROUP_NOT_FOUND, "Group not found: " + groupId.value()));

        if (group.getStatus() != GroupStatus.ACTIVE) {
            throw new BusinessRuleException(ErrorCode.GROUP_INACTIVE, "Cannot update schedules of an inactive group");
        }

        List<Schedule> newSchedules = schedules.stream()
                .map(sd -> new Schedule(
                        ScheduleId.generate(),
                        sd.dayOfWeek(),
                        sd.startTime(),
                        sd.endTime(),
                        sd.classroom()))
                .toList();

        var updated = group.changeSchedules(newSchedules, clock.instant());
        groupRepository.save(updated);

        return GroupResult.from(updated);
    }
}
