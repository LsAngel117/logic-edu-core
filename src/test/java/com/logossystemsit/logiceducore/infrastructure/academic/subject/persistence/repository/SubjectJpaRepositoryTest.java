package com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.entity.SubjectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("SubjectJpaRepository")
class SubjectJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubjectJpaRepository repository;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final String SCHOOL_ID = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM SubjectEntity")
                .executeUpdate();
    }

    @Test
    @DisplayName("should persist and find by id")
    void shouldPersistAndFindById() {
        SubjectEntity entity = buildEntity("sub-001", SCHOOL_ID, "MAT101", "Mathematics", 120);

        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<SubjectEntity> found = repository.findById("sub-001");
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("MAT101");
        assertThat(found.get().getName()).isEqualTo("Mathematics");
        assertThat(found.get().getHours()).isEqualTo(120);
        assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("should find all by school id")
    void shouldFindAllBySchoolId() {
        SubjectEntity sub1 = buildEntity("sub-001", SCHOOL_ID, "MAT101", "Mathematics", 120);
        SubjectEntity sub2 = buildEntity("sub-002", SCHOOL_ID, "PHY201", "Physics", 80);

        entityManager.persist(sub1);
        entityManager.persist(sub2);
        entityManager.flush();
        entityManager.clear();

        List<SubjectEntity> found = repository.findBySchoolId(SCHOOL_ID);
        assertThat(found).hasSize(2);
        assertThat(found).extracting(SubjectEntity::getCode)
                .containsExactlyInAnyOrder("MAT101", "PHY201");
    }

    @Test
    @DisplayName("should return empty list when no subjects for school")
    void shouldReturnEmptyListWhenNoSubjectsForSchool() {
        List<SubjectEntity> found = repository.findBySchoolId("non-existent-school");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should check existence by school and code")
    void shouldCheckExistenceBySchoolAndCode() {
        SubjectEntity entity = buildEntity("sub-001", SCHOOL_ID, "MAT101", "Mathematics", 120);
        entityManager.persistAndFlush(entity);

        boolean exists = repository.existsBySchoolIdAndCode(SCHOOL_ID, "MAT101");
        assertThat(exists).isTrue();

        boolean notExists = repository.existsBySchoolIdAndCode(SCHOOL_ID, "NONEXISTENT");
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("should persist subject with null description")
    void shouldPersistSubjectWithNullDescription() {
        SubjectEntity entity = buildEntity("sub-001", SCHOOL_ID, "MAT101", "Mathematics", 120);
        entity.setDescription(null);

        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<SubjectEntity> found = repository.findById("sub-001");
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isNull();
    }

    private SubjectEntity buildEntity(String id, String schoolId, String code, String name, int hours) {
        SubjectEntity e = new SubjectEntity();
        e.setId(id);
        e.setSchoolId(schoolId);
        e.setCode(code);
        e.setName(name);
        e.setDescription(null);
        e.setHours(hours);
        e.setStatus("ACTIVE");
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
