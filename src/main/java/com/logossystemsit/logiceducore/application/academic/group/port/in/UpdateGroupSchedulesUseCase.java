package com.logossystemsit.logiceducore.application.academic.group.port.in;

import com.logossystemsit.logiceducore.application.academic.group.dto.command.ScheduleData;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;

import java.util.List;

public interface UpdateGroupSchedulesUseCase {

    GroupResult execute(GroupId groupId, List<ScheduleData> schedules);
}
