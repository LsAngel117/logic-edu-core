package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.command.CreateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.CreateUserResult;
import com.logossystemsit.logiceducore.application.user.port.in.CreateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.Username;
import com.logossystemsit.logiceducore.domain.user.service.UserCreationPolicy;
import com.logossystemsit.logiceducore.domain.user.service.UsernameGenerator;

import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final Clock clock;
    private final UserCreationPolicy policy;
    private final UsernameGenerator usernameGenerator;

    public CreateUserService(UserRepository userRepository,
                             MembershipRepository membershipRepository,
                             Clock clock,
                             UserCreationPolicy policy,
                             UsernameGenerator usernameGenerator) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.clock = clock;
        this.policy = policy;
        this.usernameGenerator = usernameGenerator;
    }

    @Override
    @Transactional
    public CreateUserResult execute(CreateUserCommand command) {

        Instant now = clock.instant();
        LocalDate today = LocalDate.now(clock);

        List<String> candidates = usernameGenerator.generate(command.name());

        String availableUsername = candidates.stream()
                .filter(candidate -> !userRepository.existsByUsername(new Username(candidate)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available usernames"));

        Username username = new Username(availableUsername);

        // 0. Validar política de creación (CC vs TI age rules)
        policy.validate(command.document(), command.birthDate(), today);

        // 1. Crear User
        User user = User.create(
                command.userId(),
                username,
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