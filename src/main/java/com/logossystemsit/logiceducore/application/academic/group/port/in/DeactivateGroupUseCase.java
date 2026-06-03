package com.logossystemsit.logiceducore.application.academic.group.port.in;

import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;

public interface DeactivateGroupUseCase {

    GroupResult execute(GroupId id);
}
