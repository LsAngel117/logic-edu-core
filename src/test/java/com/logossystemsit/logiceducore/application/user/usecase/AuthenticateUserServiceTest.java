package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.command.LoginCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.LoginResult;
import com.logossystemsit.logiceducore.application.user.port.in.AuthenticateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.infrastructure.security.service.JwtService;
import com.logossystemsit.logiceducore.infrastructure.security.service.MembershipClaim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthenticateUserUseCase useCase;

    private static final UserId USER_ID = new UserId("123e4567-e89b-12d3-a456-426614174000");
    private static final String RAW_PASSWORD = "mySecret123";
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMifQ.signature";

    @BeforeEach
    void setUp() {
        useCase = new AuthenticateUserService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void execute_shouldAuthenticateSuccessfully() {
        // Given
        User user = buildActiveUser(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, user.getPasswordHash().value())).thenReturn(true);
        when(jwtService.generate(eq(USER_ID.value()), anyList())).thenReturn(JWT_TOKEN);

        // When
        LoginResult result = useCase.execute(new LoginCommand(USER_ID, RAW_PASSWORD));

        // Then
        assertThat(result.token()).isEqualTo(JWT_TOKEN);
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.username()).isEqualTo("jdoe123");
        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).matches(RAW_PASSWORD, user.getPasswordHash().value());
        verify(jwtService).generate(eq(USER_ID.value()), anyList());
    }

    @Test
    void execute_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new LoginCommand(USER_ID, RAW_PASSWORD)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    void execute_shouldThrowWhenPasswordDoesNotMatch() {
        User user = buildActiveUser(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, user.getPasswordHash().value())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(new LoginCommand(USER_ID, RAW_PASSWORD)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void execute_shouldGenerateTokenWithEmptyMemberships() {
        User user = buildActiveUser(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, user.getPasswordHash().value())).thenReturn(true);
        when(jwtService.generate(eq(USER_ID.value()), eq(List.of()))).thenReturn(JWT_TOKEN);

        LoginResult result = useCase.execute(new LoginCommand(USER_ID, RAW_PASSWORD));

        assertThat(result.token()).isEqualTo(JWT_TOKEN);
        verify(jwtService).generate(eq(USER_ID.value()), eq(List.of()));
    }

    @SuppressWarnings("SameParameterValue")
    private User buildActiveUser(UserId userId) {
        return User.restore(
                userId,
                new Username("jdoe123"),
                new Email("jdoe@example.com"),
                new PasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab"),
                new Name("John", null, "Doe", null),
                User.Sex.MALE,
                LocalDate.of(1990, 1, 15),
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                User.Status.ACTIVE,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z")
        );
    }
}
