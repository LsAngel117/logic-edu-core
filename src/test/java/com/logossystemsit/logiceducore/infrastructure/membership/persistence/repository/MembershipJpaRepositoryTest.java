package com.logossystemsit.logiceducore.infrastructure.membership.persistence.repository;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.entity.MembershipEntity;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MembershipJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MembershipJpaRepository repository;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createQuery("DELETE FROM MembershipEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM UserEntity").executeUpdate();
    }

    @Test
    void save_shouldPersistMembership() {
        UserEntity user = buildUser("u-1", "user1", "user1@example.com");
        entityManager.persist(user);
        entityManager.flush();

        MembershipEntity entity = buildMembership("mem-1", "u-1", "STUDENT", "COURSE", "course-1", true);
        MembershipEntity saved = repository.save(entity);

        assertThat(saved.getId()).isEqualTo("mem-1");
        assertThat(saved.getUserId()).isEqualTo("u-1");
        assertThat(saved.getRole()).isEqualTo("STUDENT");
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void findById_shouldReturnMembershipWhenFound() {
        UserEntity user = buildUser("u-2", "user2", "user2@example.com");
        entityManager.persist(user);
        MembershipEntity entity = buildMembership("mem-2", "u-2", "TEACHER", "COURSE", "course-2", true);
        entityManager.persist(entity);
        entityManager.flush();

        var result = repository.findById("mem-2");

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo("TEACHER");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        var result = repository.findById("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserId_shouldReturnAllMembershipsForUser() {
        UserEntity user = buildUser("u-3", "user3", "user3@example.com");
        entityManager.persist(user);
        MembershipEntity m1 = buildMembership("mem-3a", "u-3", "STUDENT", "COURSE", "course-a", true);
        MembershipEntity m2 = buildMembership("mem-3b", "u-3", "TEACHER", "COURSE", "course-b", true);
        entityManager.persist(m1);
        entityManager.persist(m2);
        entityManager.flush();

        List<MembershipEntity> results = repository.findByUserId("u-3");

        assertThat(results).hasSize(2);
        assertThat(results).extracting("role").containsExactlyInAnyOrder("STUDENT", "TEACHER");
    }

    @Test
    void findByUserId_shouldReturnEmptyListWhenNoMemberships() {
        List<MembershipEntity> results = repository.findByUserId("nonexistent");

        assertThat(results).isEmpty();
    }

    @Test
    void saveAndFlush_shouldPersistWithoutConstraints() {
        // FK constraint not enforced by Hibernate DDL for plain String fields.
        // FK is enforced at DB level via Flyway migrations (tested in integration).
        UserEntity user = buildUser("u-fk", "userfk", "userfk@example.com");
        entityManager.persist(user);
        entityManager.flush();

        // Save membership with valid user reference
        MembershipEntity entity = buildMembership("mem-fk", "u-fk", "STUDENT", "COURSE", "c", true);
        MembershipEntity saved = repository.saveAndFlush(entity);
        assertThat(saved.getUserId()).isEqualTo("u-fk");
    }

    private UserEntity buildUser(String id, String username, String email) {
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

    private MembershipEntity buildMembership(String id, String userId, String role, String scopeType, String scopeRefId, boolean active) {
        MembershipEntity e = new MembershipEntity();
        e.setId(id);
        e.setUserId(userId);
        e.setRole(role);
        e.setScopeType(scopeType);
        e.setScopeRefId(scopeRefId);
        e.setActive(active);
        return e;
    }
}
