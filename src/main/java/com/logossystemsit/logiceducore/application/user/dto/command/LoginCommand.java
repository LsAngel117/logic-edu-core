package com.logossystemsit.logiceducore.application.user.dto.command;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public record LoginCommand(UserId userId, String rawPassword) {
}
