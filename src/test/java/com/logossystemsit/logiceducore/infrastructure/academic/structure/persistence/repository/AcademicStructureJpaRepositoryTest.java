package com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.entity.AcademicStructureEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AcademicStructureJpaRepository")
class AcademicStructureJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AcademicStructureJpaRepository repository;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final String SCHOOL_ID = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM AcademicStructureEntity")
                .executeUpdate();
    }

    @Test
    @DisplayName("should persist and find by id")
    void shouldPersistAndFindById() {
        AcademicStructureEntity entity = buildEntity("str-1", SCHOOL_ID, "SEMESTRAL", 1);

        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<AcademicStructureEntity> found = repository.findById("str-1");
        assertThat(found).isPresent();
        assertThat(found.get().getStructureType()).isEqualTo("SEMESTRAL");
        assertThat(found.get().getVersion()).isEqualTo(1);
        assertThat(found.get().isActive()).isTrue();
    }

    @Test
    @DisplayName("should find active by school id")
    void shouldFindActiveBySchoolId() {
        AcademicStructureEntity active = buildEntity("str-1", SCHOOL_ID, "SEMESTRAL", 1);
        AcademicStructureEntity inactive = buildEntity("str-2", SCHOOL_ID, "ANUAL", 2);
        inactive.setActive(false);

        entityManager.persist(active);
        entityManager.persist(inactive);
        entityManager.flush();
        entityManager.clear();

        Optional<AcademicStructureEntity> found = repository.findBySchoolIdAndActiveTrue(SCHOOL_ID);
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("str-1");
    }

    @Test
    @DisplayName("should return empty when no active structure for school")
    void shouldReturnEmptyWhenNoActiveStructure() {
        Optional<AcademicStructureEntity> found = repository.findBySchoolIdAndActiveTrue(SCHOOL_ID);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should find latest by school id ordered by version desc")
    void shouldFindLatestBySchoolId() {
        AcademicStructureEntity v1 = buildEntity("str-1", SCHOOL_ID, "SEMESTRAL", 1);
        AcademicStructureEntity v2 = buildEntity("str-2", SCHOOL_ID, "ANUAL", 2);

        entityManager.persist(v1);
        entityManager.persist(v2);
        entityManager.flush();
        entityManager.clear();

        Optional<AcademicStructureEntity> found = repository.findTopBySchoolIdOrderByVersionDesc(SCHOOL_ID);
        assertThat(found).isPresent();
        assertThat(found.get().getVersion()).isEqualTo(2);
    }

    @Test
    @DisplayName("should find by school id")
    void shouldFindBySchoolId() {
        AcademicStructureEntity entity = buildEntity("str-1", SCHOOL_ID, "CICLO", 1);

        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();

        Optional<AcademicStructureEntity> found = repository.findBySchoolId(SCHOOL_ID);
        assertThat(found).isPresent();
        assertThat(found.get().getStructureType()).isEqualTo("CICLO");
    }

    private AcademicStructureEntity buildEntity(String id, String schoolId, String type, int version) {
        AcademicStructureEntity e = new AcademicStructureEntity();
        e.setId(id);
        e.setSchoolId(schoolId);
        e.setStructureType(type);
        e.setLevelsCount(10);
        e.setPeriodsPerLevel(2);
        e.setEvaluationPeriodsPerPeriod(3);
        e.setSubjectsPerPeriod(6);
        e.setHoursPerSubject(45);
        e.setActive(true);
        e.setVersion(version);
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
