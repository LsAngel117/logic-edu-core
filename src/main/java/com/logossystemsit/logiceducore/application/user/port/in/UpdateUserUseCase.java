package com.logossystemsit.logiceducore.application.user.port.in;

import com.logossystemsit.logiceducore.application.user.dto.command.UpdateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;

public interface UpdateUserUseCase {
    UserResult execute(UpdateUserCommand command);
}
