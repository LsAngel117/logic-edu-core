package com.logossystemsit.logiceducore.application.membership.port.in;

import com.logossystemsit.logiceducore.application.membership.dto.command.AssignMembershipCommand;

public interface AssignMembershipUseCase {
    void execute(AssignMembershipCommand command);
}
