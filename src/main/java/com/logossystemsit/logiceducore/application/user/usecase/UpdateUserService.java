package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.command.UpdateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.application.user.port.in.UpdateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final Clock clock;

    public UpdateUserService(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public UserResult execute(UpdateUserCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var updated = user.changeBasicInfo(
                command.email(),
                command.name(),
                command.sex(),
                command.birthDate(),
                command.document(),
                clock.instant()
        );

        userRepository.save(updated);
        return UserResult.from(updated);
    }
}
