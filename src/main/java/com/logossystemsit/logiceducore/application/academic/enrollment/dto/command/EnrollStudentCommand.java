package com.logossystemsit.logiceducore.application.academic.enrollment.dto.command;

import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public record EnrollStudentCommand(
        UserId userId,
        GroupId groupId
) {}
