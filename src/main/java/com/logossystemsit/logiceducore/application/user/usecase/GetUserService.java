package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.application.user.port.in.GetUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import org.springframework.transaction.annotation.Transactional;

public class GetUserService implements GetUserUseCase {

    private final UserRepository userRepository;

    public GetUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResult execute(UserId userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserResult.from(user);
    }
}
