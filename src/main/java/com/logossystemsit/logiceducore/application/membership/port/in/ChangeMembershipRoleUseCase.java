package com.logossystemsit.logiceducore.application.membership.port.in;

import com.logossystemsit.logiceducore.application.membership.dto.command.ChangeMembershipRoleCommand;

public interface ChangeMembershipRoleUseCase {
    void execute(ChangeMembershipRoleCommand command);
}
