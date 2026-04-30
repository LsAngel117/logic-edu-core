package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.command.ChangePasswordCommand;
import com.logossystemsit.logiceducore.application.user.port.in.ChangePasswordUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;

import java.time.Clock;

public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final Clock clock;

    public ChangePasswordService(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    public void execute(ChangePasswordCommand command) {

        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var updated = user.changePassword(command.newPassword(), clock.instant());

        userRepository.save(updated);
    }
}
