package com.logossystemsit.logiceducore.application.user.port.in;

import com.logossystemsit.logiceducore.application.user.dto.command.LoginCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.LoginResult;

public interface AuthenticateUserUseCase {
    LoginResult execute(LoginCommand command);
}
