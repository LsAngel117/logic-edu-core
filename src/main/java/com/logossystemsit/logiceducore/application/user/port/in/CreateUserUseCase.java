package com.logossystemsit.logiceducore.application.user.port.in;

import com.logossystemsit.logiceducore.application.user.dto.CreateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.CreateUserResult;

public interface CreateUserUseCase {

    CreateUserResult execute(CreateUserCommand command);
}
