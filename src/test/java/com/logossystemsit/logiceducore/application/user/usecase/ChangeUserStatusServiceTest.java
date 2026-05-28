package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.command.ChangeUserStatusCommand;
import com.logossystemsit.logiceducore.application.user.port.in.ChangeUserStatusUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeUserStatusServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Clock clock;

    private ChangeUserStatusUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final UserId USER_ID = new UserId("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(FIXED_NOW);
        useCase = new ChangeUserStatusService(userRepository, clock);
    }

    @Test
    void execute_shouldActivateInactiveUser() {
        User user = buildUser(User.Status.INACTIVE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        useCase.execute(new ChangeUserStatusCommand(USER_ID, User.Status.ACTIVE));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(User.Status.ACTIVE);
    }

    @Test
    void execute_shouldDeactivateActiveUser() {
        User user = buildUser(User.Status.ACTIVE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        useCase.execute(new ChangeUserStatusCommand(USER_ID, User.Status.INACTIVE));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(User.Status.INACTIVE);
    }

    @Test
    void execute_shouldBlockActiveUser() {
        User user = buildUser(User.Status.ACTIVE);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        useCase.execute(new ChangeUserStatusCommand(USER_ID, User.Status.BLOCKED));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(User.Status.BLOCKED);
    }

    @Test
    void execute_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.execute(new ChangeUserStatusCommand(USER_ID, User.Status.ACTIVE)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    void execute_shouldThrowWhenAlreadyInTargetStatus() {
        User user = buildUser(User.Status.BLOCKED);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                useCase.execute(new ChangeUserStatusCommand(USER_ID, User.Status.BLOCKED)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User already blocked");
    }

    @SuppressWarnings("SameParameterValue")
    private User buildUser(User.Status status) {
        return User.restore(
                USER_ID,
                new Username("jdoe123"),
                new Email("jdoe@example.com"),
                new PasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab"),
                new Name("John", null, "Doe", null),
                User.Sex.MALE,
                LocalDate.of(1990, 1, 15),
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                status,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-06-01T12:00:00Z")  // before FIXED_NOW
        );
    }
}
