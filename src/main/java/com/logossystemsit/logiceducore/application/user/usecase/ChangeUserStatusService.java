package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.command.ChangeUserStatusCommand;
import com.logossystemsit.logiceducore.application.user.port.in.ChangeUserStatusUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.user.model.User;

import java.time.Clock;

public class ChangeUserStatusService implements ChangeUserStatusUseCase {

    private final UserRepository userRepository;
    private final Clock clock;

    public ChangeUserStatusService(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    public void execute(ChangeUserStatusCommand command) {

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var now = clock.instant();

        User updated = switch (command.status()) {
            case ACTIVE -> user.activate(now);
            case INACTIVE -> user.deactivate(now);
            case BLOCKED -> user.block(now);
        };

        userRepository.save(updated);
    }
}
