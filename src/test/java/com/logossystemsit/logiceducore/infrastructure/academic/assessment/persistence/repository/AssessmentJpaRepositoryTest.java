package com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.entity.AssessmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AssessmentJpaRepository")
class AssessmentJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssessmentJpaRepository repository;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String GROUP_ID = "aaa00001-e29b-41d4-a716-446655440001";
    private static final String GROUP_ID_2 = "aaa00002-e29b-41d4-a716-446655440002";
    private static final String EVAL_PERIOD_ID = "eee00001-e29b-41d4-a716-446655440001";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM AssessmentEntity")
                .executeUpdate();
    }

    private AssessmentEntity buildAssessment(String id, String groupId, String name,
                                              String type, BigDecimal weight, BigDecimal maxScore,
                                              String evaluationPeriodId) {
        AssessmentEntity entity = new AssessmentEntity();
        entity.setId(id);
        entity.setGroupId(groupId);
        entity.setName(name);
        entity.setType(type);
        entity.setWeight(weight);
        entity.setMaxScore(maxScore);
        entity.setEvaluationPeriodId(evaluationPeriodId);
        entity.setCreatedAt(FIXED_NOW);
        entity.setUpdatedAt(FIXED_NOW);
        return entity;
    }

    @Test
    @DisplayName("should save and find by ID")
    void shouldSaveAndFindById() {
        AssessmentEntity entity = buildAssessment(
                "asmt-001", GROUP_ID, "Math Quiz 1", "QUIZ",
                new BigDecimal("15.00"), new BigDecimal("100.00"), EVAL_PERIOD_ID);
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<AssessmentEntity> found = repository.findById("asmt-001");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Math Quiz 1");
        assertThat(found.get().getType()).isEqualTo("QUIZ");
        assertThat(found.get().getGroupId()).isEqualTo(GROUP_ID);
        assertThat(found.get().getWeight()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("should find by group ID")
    void shouldFindByGroupId() {
        entityManager.persist(buildAssessment("asmt-001", GROUP_ID, "Quiz 1", "QUIZ",
                new BigDecimal("15.00"), new BigDecimal("100.00"), EVAL_PERIOD_ID));
        entityManager.persist(buildAssessment("asmt-002", GROUP_ID, "Exam", "EXAM",
                new BigDecimal("30.00"), new BigDecimal("100.00"), null));
        entityManager.flush();
        entityManager.clear();

        List<AssessmentEntity> results = repository.findByGroupId(GROUP_ID);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(AssessmentEntity::getId)
                .containsExactlyInAnyOrder("asmt-001", "asmt-002");
    }

    @Test
    @DisplayName("should return empty list for unknown group")
    void shouldReturnEmptyListForUnknownGroup() {
        List<AssessmentEntity> results = repository.findByGroupId("unknown-group");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should return true when name exists in group")
    void shouldReturnTrueWhenNameExistsInGroup() {
        entityManager.persist(buildAssessment("asmt-001", GROUP_ID, "Math Quiz 1", "QUIZ",
                new BigDecimal("15.00"), new BigDecimal("100.00"), EVAL_PERIOD_ID));
        entityManager.flush();
        entityManager.clear();

        boolean exists = repository.existsByGroupIdAndName(GROUP_ID, "Math Quiz 1");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("should return false when name does not exist in group")
    void shouldReturnFalseWhenNameNotExistsInGroup() {
        entityManager.persist(buildAssessment("asmt-001", GROUP_ID, "Math Quiz 1", "QUIZ",
                new BigDecimal("15.00"), new BigDecimal("100.00"), EVAL_PERIOD_ID));
        entityManager.flush();
        entityManager.clear();

        boolean exists = repository.existsByGroupIdAndName(GROUP_ID, "Nonexistent");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("should return false for name in different group")
    void shouldReturnFalseForNameInDifferentGroup() {
        entityManager.persist(buildAssessment("asmt-001", GROUP_ID, "Math Quiz 1", "QUIZ",
                new BigDecimal("15.00"), new BigDecimal("100.00"), EVAL_PERIOD_ID));
        entityManager.flush();
        entityManager.clear();

        boolean exists = repository.existsByGroupIdAndName(GROUP_ID_2, "Math Quiz 1");
        assertThat(exists).isFalse();
    }
}
