package com.logossystemsit.logiceducore.application.branch.port.in;

import com.logossystemsit.logiceducore.application.branch.dto.command.UpdateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;

public interface UpdateBranchUseCase {

    BranchResult execute(UpdateBranchCommand command);
}
