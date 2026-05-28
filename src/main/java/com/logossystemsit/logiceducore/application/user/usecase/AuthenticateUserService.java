package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.command.LoginCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.LoginResult;
import com.logossystemsit.logiceducore.application.user.port.in.AuthenticateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.infrastructure.security.service.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticateUserService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public LoginResult execute(LoginCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean matches = passwordEncoder.matches(
                command.rawPassword(),
                user.getPasswordHash().value()
        );

        if (!matches) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generate(user.getId().value(), List.of());

        return new LoginResult(token, user.getId(), user.getUsername().getValue());
    }
}
