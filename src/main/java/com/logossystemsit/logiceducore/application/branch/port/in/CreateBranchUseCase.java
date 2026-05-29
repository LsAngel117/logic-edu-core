package com.logossystemsit.logiceducore.application.branch.port.in;

import com.logossystemsit.logiceducore.application.branch.dto.command.CreateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;

public interface CreateBranchUseCase {

    BranchResult execute(CreateBranchCommand command);
}
