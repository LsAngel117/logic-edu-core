package com.logossystemsit.logiceducore.application.user.usecase;

import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.application.user.port.in.ListUsersUseCase;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListUsersServiceTest {

    @Mock
    private UserRepository userRepository;

    private ListUsersUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListUsersService(userRepository);
    }

    @Test
    void execute_shouldReturnAllUsers() {
        User user1 = buildUser("11111111-1111-1111-1111-111111111111", "alice", "alice@example.com");
        User user2 = buildUser("22222222-2222-2222-2222-222222222222", "bob", "bob@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResult> results = useCase.execute();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).id()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(results.get(0).username()).isEqualTo("alice");
        assertThat(results.get(1).id()).isEqualTo("22222222-2222-2222-2222-222222222222");
        assertThat(results.get(1).username()).isEqualTo("bob");
        verify(userRepository).findAll();
    }

    @Test
    void execute_shouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResult> results = useCase.execute();

        assertThat(results).isEmpty();
        verify(userRepository).findAll();
    }

    @Test
    void execute_shouldMapUserToUserResultCorrectly() {
        User user = buildUser("33333333-3333-3333-3333-333333333333", "charlie", "charlie@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResult> results = useCase.execute();

        assertThat(results).hasSize(1);
        UserResult result = results.get(0);
        assertThat(result.id()).isEqualTo("33333333-3333-3333-3333-333333333333");
        assertThat(result.username()).isEqualTo("charlie");
        assertThat(result.email()).isEqualTo("charlie@example.com");
        assertThat(result.firstName()).isEqualTo("Charlie");
        assertThat(result.lastName()).isEqualTo("Brown");
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @SuppressWarnings("SameParameterValue")
    private User buildUser(String id, String username, String email) {
        return User.restore(
                new UserId(id),
                new Username(username),
                new Email(email),
                new PasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab"),
                new Name("Charlie", null, "Brown", null),
                User.Sex.MALE,
                LocalDate.of(1990, 1, 15),
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                User.Status.ACTIVE,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z")
        );
    }
}
