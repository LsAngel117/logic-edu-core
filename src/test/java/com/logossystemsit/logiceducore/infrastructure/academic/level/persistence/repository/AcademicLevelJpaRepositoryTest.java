package com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.entity.AcademicLevelEntity;
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
@DisplayName("AcademicLevelJpaRepository")
class AcademicLevelJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AcademicLevelJpaRepository repository;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final String SCHOOL_ID = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM AcademicLevelEntity")
                .executeUpdate();
    }

    @Test
    @DisplayName("should persist and find by id")
    void shouldPersistAndFindById() {
        AcademicLevelEntity entity = buildEntity("level-1", SCHOOL_ID, "Primaria", 1);

        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<AcademicLevelEntity> found = repository.findById("level-1");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Primaria");
        assertThat(found.get().getNumber()).isEqualTo(1);
        assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("should find all by school id")
    void shouldFindAllBySchoolId() {
        AcademicLevelEntity level1 = buildEntity("level-1", SCHOOL_ID, "Primaria", 1);
        AcademicLevelEntity level2 = buildEntity("level-2", SCHOOL_ID, "Secundaria", 2);

        entityManager.persist(level1);
        entityManager.persist(level2);
        entityManager.flush();
        entityManager.clear();

        List<AcademicLevelEntity> found = repository.findAllBySchoolId(SCHOOL_ID);
        assertThat(found).hasSize(2);
        assertThat(found).extracting(AcademicLevelEntity::getName)
                .containsExactlyInAnyOrder("Primaria", "Secundaria");
    }

    @Test
    @DisplayName("should return empty list when no levels for school")
    void shouldReturnEmptyListWhenNoLevelsForSchool() {
        List<AcademicLevelEntity> found = repository.findAllBySchoolId("non-existent-school");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should check existence by school and number")
    void shouldCheckExistenceBySchoolAndNumber() {
        AcademicLevelEntity entity = buildEntity("level-1", SCHOOL_ID, "Primaria", 1);
        entityManager.persistAndFlush(entity);

        boolean exists = repository.existsBySchoolIdAndNumber(SCHOOL_ID, 1);
        assertThat(exists).isTrue();

        boolean notExists = repository.existsBySchoolIdAndNumber(SCHOOL_ID, 99);
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("should check existence of active periods by level id")
    void shouldCheckExistenceOfActivePeriodsByLevelId() {
        // TODO: when academic_periods table is available, this should query it.
        // For now, always returns false.
        boolean exists = repository.existsActivePeriodsByLevelId("level-1");
        assertThat(exists).isFalse();
    }

    private AcademicLevelEntity buildEntity(String id, String schoolId, String name, int number) {
        AcademicLevelEntity e = new AcademicLevelEntity();
        e.setId(id);
        e.setSchoolId(schoolId);
        e.setName(name);
        e.setNumber(number);
        e.setStatus("ACTIVE");
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
