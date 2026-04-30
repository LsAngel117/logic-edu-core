package com.logossystemsit.logiceducore.application.user.port.in;

import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public interface GetUserUseCase {
    UserResult execute(UserId userId);
}
