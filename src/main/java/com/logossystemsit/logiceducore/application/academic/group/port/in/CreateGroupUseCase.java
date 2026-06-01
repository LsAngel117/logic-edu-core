package com.logossystemsit.logiceducore.application.academic.group.port.in;

import com.logossystemsit.logiceducore.application.academic.group.dto.command.CreateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;

public interface CreateGroupUseCase {

    GroupResult execute(CreateGroupCommand command);
}
