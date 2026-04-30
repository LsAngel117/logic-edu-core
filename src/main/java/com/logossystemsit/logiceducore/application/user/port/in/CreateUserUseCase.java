package com.logossystemsit.logiceducore.application.user.port.in;

import com.logossystemsit.logiceducore.application.user.dto.command.CreateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.CreateUserResult;

public interface CreateUserUseCase {

    CreateUserResult execute(CreateUserCommand command);
}
