package com.logossystemsit.logiceducore.application.membership.port.in;

import com.logossystemsit.logiceducore.application.membership.dto.ChangeMembershipScopeCommand;

public interface ChangeMembershipScopeUseCase {
    void execute(ChangeMembershipScopeCommand command);
}
