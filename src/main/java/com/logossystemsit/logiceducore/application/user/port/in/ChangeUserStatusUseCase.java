package com.logossystemsit.logiceducore.application.user.port.in;

import com.logossystemsit.logiceducore.application.user.dto.ChangeUserStatusCommand;

public interface ChangeUserStatusUseCase {
    void execute(ChangeUserStatusCommand command);
}
