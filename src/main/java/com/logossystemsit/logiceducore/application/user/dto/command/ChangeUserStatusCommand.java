package com.logossystemsit.logiceducore.application.user.dto.command;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public record ChangeUserStatusCommand(
        UserId userId,
        User.Status status
) {}
