package com.logossystemsit.logiceducore.application.user.port.in;

import com.logossystemsit.logiceducore.application.user.dto.ChangePasswordCommand;

public interface ChangePasswordUseCase {
    void execute(ChangePasswordCommand command);
}
