package com.logossystemsit.logiceducore.application.membership.port.in;

import com.logossystemsit.logiceducore.application.membership.dto.AssignMembershipCommand;

public interface AssignMembershipUseCase {
    void execute(AssignMembershipCommand command);
}
