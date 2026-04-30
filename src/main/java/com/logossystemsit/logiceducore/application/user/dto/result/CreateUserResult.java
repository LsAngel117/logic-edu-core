package com.logossystemsit.logiceducore.application.user.dto.result;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public record CreateUserResult(
        UserId userId,
        String username
) {}
