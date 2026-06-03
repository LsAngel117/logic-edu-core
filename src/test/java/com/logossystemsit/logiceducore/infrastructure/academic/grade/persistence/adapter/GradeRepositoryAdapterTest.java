package com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.entity.GradeEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.grade.persistence.repository.GradeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GradeRepositoryAdapter")
class GradeRepositoryAdapterTest {

    @Mock
    private GradeJpaRepository jpa;

    private GradeRepository adapter;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final GradeId GRADE_ID = GradeId.generate();
    private static final AssessmentId ASSESSMENT_ID = AssessmentId.generate();
    private static final UserId STUDENT_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final BigDecimal VALUE = new BigDecimal("8.50");

    @BeforeEach
    void setUp() {
        adapter = new GradeRepositoryAdapter(jpa);
    }

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("should map grade to entity and save")
        void shouldMapAndSave() {
            Grade grade = Grade.create(
                    GRADE_ID, ASSESSMENT_ID, STUDENT_ID, VALUE, FIXED_NOW
            );

            adapter.save(grade);

            ArgumentCaptor<GradeEntity> captor = ArgumentCaptor.forClass(GradeEntity.class);
            verify(jpa).save(captor.capture());
            GradeEntity entity = captor.getValue();

            assertThat(entity.getId()).isEqualTo(GRADE_ID.value());
            assertThat(entity.getAssessmentId()).isEqualTo(ASSESSMENT_ID.value());
            assertThat(entity.getStudentId()).isEqualTo(STUDENT_ID.value());
            assertThat(entity.getValue()).isEqualByComparingTo(VALUE);
            assertThat(entity.getGradedAt()).isEqualTo(FIXED_NOW);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_NOW);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("should find by ID and map to domain")
        void shouldFindByIdAndMapToDomain() {
            GradeEntity entity = createSampleEntity();
            when(jpa.findById(GRADE_ID.value())).thenReturn(Optional.of(entity));

            Optional<Grade> result = adapter.findById(GRADE_ID);

            assertThat(result).isPresent();
            Grade domain = result.get();
            assertThat(domain.getId()).isEqualTo(GRADE_ID);
            assertThat(domain.getValue()).isEqualByComparingTo(VALUE);
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(jpa.findById("nonexistent")).thenReturn(Optional.empty());

            Optional<Grade> result = adapter.findById(new GradeId("nonexistent"));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByAssessmentId")
    class FindByAssessmentIdTests {

        @Test
        @DisplayName("should find by assessment ID")
        void shouldFindByAssessmentId() {
            GradeEntity entity = createSampleEntity();
            when(jpa.findByAssessmentId(ASSESSMENT_ID.value())).thenReturn(List.of(entity));

            List<Grade> results = adapter.findByAssessmentId(ASSESSMENT_ID);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getAssessmentId()).isEqualTo(ASSESSMENT_ID);
        }
    }

    @Nested
    @DisplayName("findByAssessmentIdAndStudentId")
    class FindByAssessmentIdAndStudentIdTests {

        @Test
        @DisplayName("should find by assessment and student")
        void shouldFindByAssessmentAndStudent() {
            GradeEntity entity = createSampleEntity();
            when(jpa.findByAssessmentIdAndStudentId(ASSESSMENT_ID.value(), STUDENT_ID.value()))
                    .thenReturn(Optional.of(entity));

            Optional<Grade> result = adapter.findByAssessmentIdAndStudentId(ASSESSMENT_ID, STUDENT_ID);

            assertThat(result).isPresent();
            assertThat(result.get().getStudentId()).isEqualTo(STUDENT_ID);
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenStudentNotFound() {
            when(jpa.findByAssessmentIdAndStudentId(any(), any()))
                    .thenReturn(Optional.empty());

            Optional<Grade> result = adapter.findByAssessmentIdAndStudentId(
                    ASSESSMENT_ID, new UserId("770e8400-e29b-41d4-a716-446655440002"));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByAssessmentId")
    class CountByAssessmentIdTests {

        @Test
        @DisplayName("should count grades for assessment")
        void shouldCountGradesForAssessment() {
            when(jpa.countByAssessmentId(ASSESSMENT_ID.value())).thenReturn(5L);

            long count = adapter.countByAssessmentId(ASSESSMENT_ID);

            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("should return zero when no grades")
        void shouldReturnZeroWhenNoGrades() {
            when(jpa.countByAssessmentId(ASSESSMENT_ID.value())).thenReturn(0L);

            long count = adapter.countByAssessmentId(ASSESSMENT_ID);

            assertThat(count).isZero();
        }
    }

    // ======================== helpers ========================

    private GradeEntity createSampleEntity() {
        GradeEntity entity = new GradeEntity();
        entity.setId(GRADE_ID.value());
        entity.setAssessmentId(ASSESSMENT_ID.value());
        entity.setStudentId(STUDENT_ID.value());
        entity.setValue(VALUE);
        entity.setGradedAt(FIXED_NOW);
        entity.setUpdatedAt(FIXED_NOW);
        return entity;
    }
}
