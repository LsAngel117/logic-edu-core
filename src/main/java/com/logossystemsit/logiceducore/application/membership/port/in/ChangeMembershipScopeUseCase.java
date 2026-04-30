package com.logossystemsit.logiceducore.application.membership.port.in;

import com.logossystemsit.logiceducore.application.membership.dto.command.ChangeMembershipScopeCommand;

public interface ChangeMembershipScopeUseCase {
    void execute(ChangeMembershipScopeCommand command);
}
