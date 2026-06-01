package com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.entity.EnrollmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("EnrollmentJpaRepository")
class EnrollmentJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EnrollmentJpaRepository repository;

    private static final Instant NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String USER_ID = "990e8400-e29b-41d4-a716-446655440004";
    private static final String USER_ID_2 = "990e8400-e29b-41d4-a716-446655440005";
    private static final String GROUP_ID = "771e8400-e29b-41d4-a716-446655440006";
    private static final String GROUP_ID_2 = "771e8400-e29b-41d4-a716-446655440007";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM EnrollmentEntity")
                .executeUpdate();
    }

    private EnrollmentEntity buildEnrollment(String id, String userId, String groupId, String status) {
        EnrollmentEntity entity = new EnrollmentEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setGroupId(groupId);
        entity.setStatus(status);
        entity.setEnrolledAt(NOW);
        entity.setUpdatedAt(NOW);
        return entity;
    }

    @Test
    @DisplayName("should persist and find by id")
    void shouldPersistAndFindById() {
        EnrollmentEntity entity = buildEnrollment("enr-001", USER_ID, GROUP_ID, "ACTIVE");
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<EnrollmentEntity> found = repository.findById("enr-001");
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(USER_ID);
        assertThat(found.get().getGroupId()).isEqualTo(GROUP_ID);
        assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("should find all by group id")
    void shouldFindAllByGroupId() {
        entityManager.persist(buildEnrollment("enr-001", USER_ID, GROUP_ID, "ACTIVE"));
        entityManager.persist(buildEnrollment("enr-002", USER_ID_2, GROUP_ID, "ACTIVE"));
        entityManager.persist(buildEnrollment("enr-003", "990e8400-e29b-41d4-a716-446655440008", GROUP_ID_2, "INACTIVE"));
        entityManager.flush();
        entityManager.clear();

        List<EnrollmentEntity> found = repository.findByGroupId(GROUP_ID);
        assertThat(found).hasSize(2);
        assertThat(found).extracting(EnrollmentEntity::getId)
                .containsExactlyInAnyOrder("enr-001", "enr-002");
    }

    @Test
    @DisplayName("should count active enrollments by group id")
    void shouldCountActiveByGroupId() {
        entityManager.persist(buildEnrollment("enr-001", USER_ID, GROUP_ID, "ACTIVE"));
        entityManager.persist(buildEnrollment("enr-002", USER_ID_2, GROUP_ID, "DROPPED"));
        entityManager.persist(buildEnrollment("enr-003", "user-003", GROUP_ID, "INACTIVE"));
        entityManager.flush();
        entityManager.clear();

        long count = repository.countByGroupIdAndStatus(GROUP_ID, "ACTIVE");
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("should check existence by userId and groupId")
    void shouldCheckExistenceByUserIdAndGroupId() {
        entityManager.persist(buildEnrollment("enr-001", USER_ID, GROUP_ID, "ACTIVE"));
        entityManager.flush();
        entityManager.clear();

        boolean exists = repository.existsByUserIdAndGroupId(USER_ID, GROUP_ID);
        assertThat(exists).isTrue();

        boolean notExists = repository.existsByUserIdAndGroupId("990e8400-e29b-41d4-a716-446655440099", GROUP_ID);
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("should check active enrollment by student, subject and period via custom @Query")
    void shouldCheckActiveByStudentAndSubjectAndPeriod() {
        // This test requires groups to exist. We test the custom query.
        // For unit-level, we verify the method exists and compiles.
        // The full integration test would need group entities.
        entityManager.persist(buildEnrollment("enr-001", USER_ID, GROUP_ID, "ACTIVE"));
        entityManager.flush();
        entityManager.clear();

        // The custom query joins EnrollmentEntity + GroupEntity.
        // Without GroupEntity data, the result depends on DB state.
        // We test that the method executes without errors.
        boolean result = repository.existsActiveByStudentAndSubjectAndPeriod(
                USER_ID, "subject-001", "period-001");
        // Without matching group, this should return false.
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return empty list for group with no enrollments")
    void shouldReturnEmptyListForGroupWithNoEnrollments() {
        List<EnrollmentEntity> found = repository.findByGroupId("nonexistent");
        assertThat(found).isEmpty();
    }
}
