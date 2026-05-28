package com.logossystemsit.logiceducore.infrastructure.user.persistence.adapter;

import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.entity.UserEntity;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpa;

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(jpa);
    }

    @Test
    void findByEmail_delegatesToJpaRepositoryAndMapsResult() {
        var email = new Email("test@example.com");
        var entity = buildEntity("123e4567-e89b-12d3-a456-426614174000", "jdoe123", email.getValue());
        when(jpa.findByEmail(email.getValue())).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getId().value()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
        assertThat(result.get().getEmail().getValue()).isEqualTo("test@example.com");
        verify(jpa).findByEmail(email.getValue());
    }

    @Test
    void findByEmail_returnsEmptyWhenNotFound() {
        var email = new Email("noone@example.com");
        when(jpa.findByEmail(email.getValue())).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail(email);

        assertThat(result).isEmpty();
        verify(jpa).findByEmail(email.getValue());
    }

    @Test
    void existsByEmail_delegatesToJpaRepository() {
        var email = new Email("exists@example.com");
        when(jpa.existsByEmail(email.getValue())).thenReturn(true);

        boolean result = adapter.existsByEmail(email);

        assertThat(result).isTrue();
        verify(jpa).existsByEmail(email.getValue());
    }

    @Test
    void existsByEmail_returnsFalseWhenNotExists() {
        var email = new Email("missing@example.com");
        when(jpa.existsByEmail(email.getValue())).thenReturn(false);

        boolean result = adapter.existsByEmail(email);

        assertThat(result).isFalse();
        verify(jpa).existsByEmail(email.getValue());
    }

    @Test
    void existsByUsername_delegatesToJpaRepository() {
        var username = new Username("jdoe123");
        when(jpa.existsByUsername(username.getValue())).thenReturn(true);

        boolean result = adapter.existsByUsername(username);

        assertThat(result).isTrue();
        verify(jpa).existsByUsername(username.getValue());
    }

    @Test
    void existsByUsername_returnsFalseWhenNotExists() {
        var username = new Username("unknown");
        when(jpa.existsByUsername(username.getValue())).thenReturn(false);

        boolean result = adapter.existsByUsername(username);

        assertThat(result).isFalse();
        verify(jpa).existsByUsername(username.getValue());
    }

    private UserEntity buildEntity(String id, String username, String email) {
        UserEntity e = new UserEntity();
        e.setId(id);
        e.setUsername(username);
        e.setEmail(email);
        e.setPasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab");
        e.setFirstGivenName("John");
        e.setSecondGivenName(null);
        e.setFirstFamilyName("Doe");
        e.setSecondFamilyName(null);
        e.setSex(User.Sex.MALE);
        e.setBirthDate(LocalDate.of(1990, 1, 15));
        e.setDocumentType("CC");
        e.setDocumentValue("1234567890");
        e.setStatus(User.Status.ACTIVE);
        e.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        e.setUpdatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        return e;
    }
}
