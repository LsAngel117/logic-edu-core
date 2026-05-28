package com.logossystemsit.logiceducore.infrastructure.user.persistence.repository;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserJpaRepository repository;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createQuery("DELETE FROM UserEntity").executeUpdate();
    }

    @Test
    void save_shouldPersistUser() {
        UserEntity entity = buildEntity("user-1", "jdoe123", "jdoe@example.com");

        UserEntity saved = repository.save(entity);

        assertThat(saved.getId()).isEqualTo("user-1");
        assertThat(saved.getUsername()).isEqualTo("jdoe123");
        assertThat(saved.getEmail()).isEqualTo("jdoe@example.com");
        assertThat(saved.getStatus()).isEqualTo(User.Status.ACTIVE);
    }

    @Test
    void findById_shouldReturnUserWhenFound() {
        UserEntity entity = buildEntity("user-2", "alice", "alice@example.com");
        entityManager.persist(entity);
        entityManager.flush();

        Optional<UserEntity> result = repository.findById("user-2");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        Optional<UserEntity> result = repository.findById("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_shouldReturnUserWhenFound() {
        UserEntity entity = buildEntity("user-3", "bob99", "bob@example.com");
        entityManager.persist(entity);
        entityManager.flush();

        Optional<UserEntity> result = repository.findByUsername("bob99");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("user-3");
    }

    @Test
    void findByUsername_shouldReturnEmptyWhenNotFound() {
        Optional<UserEntity> result = repository.findByUsername("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnUserWhenFound() {
        UserEntity entity = buildEntity("user-4", "charlie", "charlie@example.com");
        entityManager.persist(entity);
        entityManager.flush();

        Optional<UserEntity> result = repository.findByEmail("charlie@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("charlie@example.com");
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenExists() {
        UserEntity entity = buildEntity("user-5", "dave", "dave@example.com");
        entityManager.persist(entity);
        entityManager.flush();

        assertThat(repository.existsByEmail("dave@example.com")).isTrue();
        assertThat(repository.existsByEmail("none@example.com")).isFalse();
    }

    @Test
    void existsByUsername_shouldReturnTrueWhenExists() {
        UserEntity entity = buildEntity("user-6", "eve88", "eve@example.com");
        entityManager.persist(entity);
        entityManager.flush();

        assertThat(repository.existsByUsername("eve88")).isTrue();
        assertThat(repository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    void uniqueConstraintOnUsername_shouldRejectDuplicate() {
        UserEntity e1 = buildEntity("user-7a", "uniqueuser", "a@example.com");
        repository.saveAndFlush(e1);

        UserEntity e2 = buildEntity("user-7b", "uniqueuser", "b@example.com");

        assertThatThrownBy(() -> repository.saveAndFlush(e2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void uniqueConstraintOnEmail_shouldRejectDuplicate() {
        UserEntity e1 = buildEntity("user-8a", "user-a", "unique@example.com");
        repository.saveAndFlush(e1);

        UserEntity e2 = buildEntity("user-8b", "user-b", "unique@example.com");

        assertThatThrownBy(() -> repository.saveAndFlush(e2))
                .isInstanceOf(DataIntegrityViolationException.class);
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
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
