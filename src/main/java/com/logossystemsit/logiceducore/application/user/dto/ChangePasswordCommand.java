package com.logossystemsit.logiceducore.application.user.dto;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.PasswordHash;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public record ChangePasswordCommand(
        UserId userId,
        PasswordHash newPassword
) {}
