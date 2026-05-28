package com.logossystemsit.logiceducore.application.user.dto.result;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public record LoginResult(String token, UserId userId, String username) {
}
