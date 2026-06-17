package com.logossystemsit.logiceducore.application.user.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.user.dto.command.ChangePasswordCommand;
import com.logossystemsit.logiceducore.application.user.port.in.ChangePasswordUseCase;
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
class ChangePasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Clock clock;

    private ChangePasswordUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final UserId USER_ID = new UserId("123e4567-e89b-12d3-a456-426614174000");
    private static final PasswordHash OLD_PASSWORD = new PasswordHash(
            "$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab");
    private static final PasswordHash NEW_PASSWORD = new PasswordHash(
            "$2a$10$zyxwvutsrqponmlkjihgfedcbaABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab");

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(FIXED_NOW);
        useCase = new ChangePasswordService(userRepository, clock);
    }

    @Test
    void execute_shouldChangePasswordSuccessfully() {
        User user = buildUser(User.Status.ACTIVE, OLD_PASSWORD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        useCase.execute(new ChangePasswordCommand(USER_ID, NEW_PASSWORD));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo(NEW_PASSWORD);
        assertThat(captor.getValue().getUpdatedAt()).isEqualTo(FIXED_NOW);
    }

    @Test
    void execute_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ChangePasswordCommand(USER_ID, NEW_PASSWORD)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void execute_shouldThrowWhenUserIsBlocked() {
        User user = buildUser(User.Status.BLOCKED, OLD_PASSWORD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.execute(new ChangePasswordCommand(USER_ID, NEW_PASSWORD)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Blocked user cannot change password");
    }

    @Test
    void execute_shouldThrowWhenSamePassword() {
        User user = buildUser(User.Status.ACTIVE, OLD_PASSWORD);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.execute(new ChangePasswordCommand(USER_ID, OLD_PASSWORD)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("New password cannot be the same as the current one");
    }

    @SuppressWarnings("SameParameterValue")
    private User buildUser(User.Status status, PasswordHash password) {
        return User.restore(
                USER_ID,
                new Username("jdoe123"),
                new Email("jdoe@example.com"),
                password,
                new Name("John", null, "Doe", null),
                User.Sex.MALE,
                LocalDate.of(1990, 1, 15),
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                null, null, null, null,
                status,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-06-01T12:00:00Z")
        );
    }
}
