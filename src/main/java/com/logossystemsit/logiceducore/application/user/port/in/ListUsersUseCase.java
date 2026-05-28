package com.logossystemsit.logiceducore.application.user.port.in;

import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;

import java.util.List;

public interface ListUsersUseCase {
    List<UserResult> execute();
}
