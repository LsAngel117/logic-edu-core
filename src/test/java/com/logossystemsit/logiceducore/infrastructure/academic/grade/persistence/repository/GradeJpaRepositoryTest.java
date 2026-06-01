package com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.entity.GradeEntity;
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
@DisplayName("GradeJpaRepository")
class GradeJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GradeJpaRepository repository;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String ASSESSMENT_ID = "aaa00001-e29b-41d4-a716-446655440001";
    private static final String STUDENT_ID_1 = "990e8400-e29b-41d4-a716-446655440004";
    private static final String STUDENT_ID_2 = "880e8400-e29b-41d4-a716-446655440003";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM GradeEntity")
                .executeUpdate();
    }

    private GradeEntity buildGrade(String id, String assessmentId, String studentId, BigDecimal value) {
        GradeEntity entity = new GradeEntity();
        entity.setId(id);
        entity.setAssessmentId(assessmentId);
        entity.setStudentId(studentId);
        entity.setValue(value);
        entity.setGradedAt(FIXED_NOW);
        entity.setUpdatedAt(FIXED_NOW);
        return entity;
    }

    @Test
    @DisplayName("should save and find by ID")
    void shouldSaveAndFindById() {
        GradeEntity entity = buildGrade("grd-001", ASSESSMENT_ID, STUDENT_ID_1, new BigDecimal("8.50"));
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<GradeEntity> found = repository.findById("grd-001");
        assertThat(found).isPresent();
        assertThat(found.get().getValue()).isEqualByComparingTo(new BigDecimal("8.50"));
        assertThat(found.get().getAssessmentId()).isEqualTo(ASSESSMENT_ID);
        assertThat(found.get().getStudentId()).isEqualTo(STUDENT_ID_1);
    }

    @Test
    @DisplayName("should find by assessment ID")
    void shouldFindByAssessmentId() {
        entityManager.persist(buildGrade("grd-001", ASSESSMENT_ID, STUDENT_ID_1, new BigDecimal("8.50")));
        entityManager.persist(buildGrade("grd-002", ASSESSMENT_ID, STUDENT_ID_2, new BigDecimal("6.00")));
        entityManager.flush();
        entityManager.clear();

        List<GradeEntity> results = repository.findByAssessmentId(ASSESSMENT_ID);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(GradeEntity::getId)
                .containsExactlyInAnyOrder("grd-001", "grd-002");
    }

    @Test
    @DisplayName("should return empty list for unknown assessment")
    void shouldReturnEmptyListForUnknownAssessment() {
        List<GradeEntity> results = repository.findByAssessmentId("unknown-assessment");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should find by assessment and student")
    void shouldFindByAssessmentAndStudent() {
        entityManager.persist(buildGrade("grd-001", ASSESSMENT_ID, STUDENT_ID_1, new BigDecimal("8.50")));
        entityManager.flush();
        entityManager.clear();

        Optional<GradeEntity> found = repository.findByAssessmentIdAndStudentId(ASSESSMENT_ID, STUDENT_ID_1);
        assertThat(found).isPresent();
        assertThat(found.get().getStudentId()).isEqualTo(STUDENT_ID_1);
    }

    @Test
    @DisplayName("should return empty when student not found")
    void shouldReturnEmptyForNonMatchingStudent() {
        entityManager.persist(buildGrade("grd-001", ASSESSMENT_ID, STUDENT_ID_1, new BigDecimal("8.50")));
        entityManager.flush();
        entityManager.clear();

        Optional<GradeEntity> found = repository.findByAssessmentIdAndStudentId(ASSESSMENT_ID, STUDENT_ID_2);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should count grades by assessment")
    void shouldCountGradesByAssessment() {
        entityManager.persist(buildGrade("grd-001", ASSESSMENT_ID, STUDENT_ID_1, new BigDecimal("8.50")));
        entityManager.persist(buildGrade("grd-002", ASSESSMENT_ID, STUDENT_ID_2, new BigDecimal("6.00")));
        entityManager.flush();
        entityManager.clear();

        long count = repository.countByAssessmentId(ASSESSMENT_ID);
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("should return zero count for unknown assessment")
    void shouldReturnZeroCountForUnknownAssessment() {
        long count = repository.countByAssessmentId("unknown-assessment");
        assertThat(count).isZero();
    }
}
