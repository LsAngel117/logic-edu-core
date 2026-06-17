package com.logossystemsit.logiceducore.application.user.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.application.user.port.in.GetUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private GetUserUseCase useCase;

    private static final UserId USER_ID = new UserId("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setUp() {
        useCase = new GetUserService(userRepository);
    }

    @Test
    void execute_shouldReturnUserWhenFound() {
        User user = buildUser(USER_ID, "jdoe123", "jdoe@example.com");
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserResult result = useCase.execute(USER_ID);

        assertThat(result.id()).isEqualTo(USER_ID.value());
        assertThat(result.username()).isEqualTo("jdoe123");
        assertThat(result.email()).isEqualTo("jdoe@example.com");
        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void execute_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @SuppressWarnings("SameParameterValue")
    private User buildUser(UserId id, String username, String email) {
        return User.restore(
                id,
                new Username(username),
                new Email(email),
                new PasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab"),
                new Name("John", null, "Doe", null),
                User.Sex.MALE,
                LocalDate.of(1990, 1, 15),
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                null, null, null, null,
                User.Status.ACTIVE,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z")
        );
    }
}
