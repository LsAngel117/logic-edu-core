package com.logossystemsit.logiceducore.application.membership.port.in;

import com.logossystemsit.logiceducore.application.membership.dto.result.MembershipResult;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.util.List;

public interface GetUserMembershipsUseCase {
    List<MembershipResult> execute(UserId userId);
}
