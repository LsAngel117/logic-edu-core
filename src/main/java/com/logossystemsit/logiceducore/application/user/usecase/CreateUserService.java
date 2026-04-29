package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.CreateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.CreateUserResult;
import com.logossystemsit.logiceducore.application.user.port.in.CreateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;

import java.time.Clock;
import java.time.Instant;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final Clock clock;

    public CreateUserService(UserRepository userRepository,
                             MembershipRepository membershipRepository,
                             Clock clock) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.clock = clock;
    }

    @Override
    public CreateUserResult execute(CreateUserCommand command) {

        Instant now = clock.instant();

        // 1. Crear User
        User user = User.create(
                command.userId(),
                command.username(),
                command.email(),
                command.passwordHash(),
                command.name(),
                command.sex(),
                command.birthDate(),
                command.document(),
                now
        );

        // 2. Crear Membership
        Membership membership = Membership.create(
                user.getId(),
                command.role(),
                command.scope()
        );

        // 3. Persistencia (debe ser transaccional)
        userRepository.save(user);
        membershipRepository.save(membership);

        // 4. Return
        return new CreateUserResult(
                user.getId(),
                user.getUsername().getValue()
        );
    }
}