package com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.entity.EvaluationPeriodEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("EvaluationPeriodJpaRepository")
class EvaluationPeriodJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluationPeriodJpaRepository repository;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final String PERIOD_ID = "990e8400-e29b-41d4-a716-446655440004";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM EvaluationPeriodEntity")
                .executeUpdate();
    }

    @Test
    @DisplayName("should persist and find by id")
    void shouldPersistAndFindById() {
        EvaluationPeriodEntity entity = buildEntity(
                "eval-1", PERIOD_ID, "Examen 1", 1,
                new BigDecimal("30.00"),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15), "ACTIVE"
        );

        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<EvaluationPeriodEntity> found = repository.findById("eval-1");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Examen 1");
        assertThat(found.get().getWeight()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("should find all by period id")
    void shouldFindAllByPeriodId() {
        EvaluationPeriodEntity p1 = buildEntity(
                "eval-1", PERIOD_ID, "Examen 1", 1,
                new BigDecimal("30.00"),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15), "ACTIVE"
        );
        EvaluationPeriodEntity p2 = buildEntity(
                "eval-2", PERIOD_ID, "Examen 2", 2,
                new BigDecimal("40.00"),
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 15), "ACTIVE"
        );

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();
        entityManager.clear();

        List<EvaluationPeriodEntity> found = repository.findByPeriodId(PERIOD_ID);
        assertThat(found).hasSize(2);
        assertThat(found).extracting(EvaluationPeriodEntity::getName)
                .containsExactlyInAnyOrder("Examen 1", "Examen 2");
    }

    @Test
    @DisplayName("should sum weights by period id")
    void shouldSumWeightsByPeriodId() {
        EvaluationPeriodEntity p1 = buildEntity(
                "eval-1", PERIOD_ID, "Examen 1", 1,
                new BigDecimal("30.50"),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15), "ACTIVE"
        );
        EvaluationPeriodEntity p2 = buildEntity(
                "eval-2", PERIOD_ID, "Examen 2", 2,
                new BigDecimal("40.25"),
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 15), "ACTIVE"
        );

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();
        entityManager.clear();

        BigDecimal sum = repository.sumWeightsByPeriodId(PERIOD_ID);
        assertThat(sum).isEqualByComparingTo(new BigDecimal("70.75"));
    }

    @Test
    @DisplayName("should return zero when no weights to sum")
    void shouldReturnZeroWhenNoWeights() {
        BigDecimal sum = repository.sumWeightsByPeriodId("non-existent-period");
        assertThat(sum).isNotNull();
        assertThat(sum.compareTo(BigDecimal.ZERO)).isEqualTo(0);
    }

    @Test
    @DisplayName("should find by period id for different periods independently")
    void shouldFindByPeriodIdIndependently() {
        String otherPeriodId = "880e8400-e29b-41d4-a716-446655440005";

        EvaluationPeriodEntity p1 = buildEntity(
                "eval-1", PERIOD_ID, "Eval A", 1,
                new BigDecimal("50.00"),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15), "ACTIVE"
        );
        EvaluationPeriodEntity p2 = buildEntity(
                "eval-2", otherPeriodId, "Eval B", 1,
                new BigDecimal("75.00"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), "ACTIVE"
        );

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findByPeriodId(PERIOD_ID)).hasSize(1);
        assertThat(repository.findByPeriodId(otherPeriodId)).hasSize(1);
    }

    private EvaluationPeriodEntity buildEntity(String id, String periodId, String name,
                                                int sequence, BigDecimal weight,
                                                LocalDate startDate, LocalDate endDate,
                                                String status) {
        EvaluationPeriodEntity e = new EvaluationPeriodEntity();
        e.setId(id);
        e.setPeriodId(periodId);
        e.setName(name);
        e.setSequence(sequence);
        e.setWeight(weight);
        e.setStartDate(startDate);
        e.setEndDate(endDate);
        e.setStatus(status);
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
