package com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.entity.AcademicPeriodEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AcademicPeriodJpaRepository")
class AcademicPeriodJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AcademicPeriodJpaRepository repository;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final String LEVEL_ID = "770e8400-e29b-41d4-a716-446655440002";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM AcademicPeriodEntity")
                .executeUpdate();
    }

    @Test
    @DisplayName("should persist and find by id")
    void shouldPersistAndFindById() {
        AcademicPeriodEntity entity = buildEntity(
                "per-1", LEVEL_ID, "SEMESTER", "Primer Semestre", 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31), "ACTIVE"
        );

        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<AcademicPeriodEntity> found = repository.findById("per-1");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Primer Semestre");
        assertThat(found.get().getPeriodType()).isEqualTo("SEMESTER");
        assertThat(found.get().getSequence()).isEqualTo(1);
        assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("should find all by level id")
    void shouldFindAllByLevelId() {
        AcademicPeriodEntity p1 = buildEntity(
                "per-1", LEVEL_ID, "SEMESTER", "Primer Semestre", 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31), "ACTIVE"
        );
        AcademicPeriodEntity p2 = buildEntity(
                "per-2", LEVEL_ID, "SEMESTER", "Segundo Semestre", 2,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 11, 30), "ACTIVE"
        );

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();
        entityManager.clear();

        List<AcademicPeriodEntity> found = repository.findByLevelId(LEVEL_ID);
        assertThat(found).hasSize(2);
        assertThat(found).extracting(AcademicPeriodEntity::getName)
                .containsExactlyInAnyOrder("Primer Semestre", "Segundo Semestre");
    }

    @Test
    @DisplayName("should return empty list when no periods for level")
    void shouldReturnEmptyListWhenNoPeriodsForLevel() {
        List<AcademicPeriodEntity> found = repository.findByLevelId("non-existent-level");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should find periods for different level independently")
    void shouldFindPeriodsForDifferentLevelIndependently() {
        String otherLevelId = "880e8400-e29b-41d4-a716-446655440003";

        AcademicPeriodEntity p1 = buildEntity(
                "per-1", LEVEL_ID, "SEMESTER", "Primaria", 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31), "ACTIVE"
        );
        AcademicPeriodEntity p2 = buildEntity(
                "per-2", otherLevelId, "TRIMESTER", "Otro", 1,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31), "ACTIVE"
        );

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findByLevelId(LEVEL_ID)).hasSize(1);
        assertThat(repository.findByLevelId(otherLevelId)).hasSize(1);
    }

    @Test
    @DisplayName("should persist all fields correctly")
    void shouldPersistAllFieldsCorrectly() {
        AcademicPeriodEntity entity = buildEntity(
                "per-3", LEVEL_ID, "ANUAL", "Ciclo Anual", 1,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "INACTIVE"
        );

        entityManager.persistAndFlush(entity);
        entityManager.clear();

        AcademicPeriodEntity found = repository.findById("per-3").orElseThrow();
        assertThat(found.getPeriodType()).isEqualTo("ANUAL");
        assertThat(found.getName()).isEqualTo("Ciclo Anual");
        assertThat(found.getStatus()).isEqualTo("INACTIVE");
        assertThat(found.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(found.getEndDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getUpdatedAt()).isNotNull();
    }

    private AcademicPeriodEntity buildEntity(String id, String levelId, String periodType,
                                              String name, int sequence,
                                              LocalDate startDate, LocalDate endDate,
                                              String status) {
        AcademicPeriodEntity e = new AcademicPeriodEntity();
        e.setId(id);
        e.setLevelId(levelId);
        e.setPeriodType(periodType);
        e.setName(name);
        e.setSequence(sequence);
        e.setStartDate(startDate);
        e.setEndDate(endDate);
        e.setStatus(status);
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
