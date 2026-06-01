package com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.AssessmentType;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.entity.AssessmentEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.assessment.persistence.repository.AssessmentJpaRepository;
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
@DisplayName("AssessmentRepositoryAdapter")
class AssessmentRepositoryAdapterTest {

    @Mock
    private AssessmentJpaRepository jpa;

    private AssessmentRepository adapter;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final AssessmentId ASSESSMENT_ID = AssessmentId.generate();
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final EvaluationPeriodId EVAL_PERIOD_ID = EvaluationPeriodId.generate();

    @BeforeEach
    void setUp() {
        adapter = new AssessmentRepositoryAdapter(jpa);
    }

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("should map assessment to entity and save")
        void shouldMapAndSave() {
            Assessment assessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, EVAL_PERIOD_ID,
                    "Math Quiz 1", AssessmentType.QUIZ,
                    new BigDecimal("15.00"), new BigDecimal("100.00"),
                    FIXED_NOW
            );

            adapter.save(assessment);

            ArgumentCaptor<AssessmentEntity> captor = ArgumentCaptor.forClass(AssessmentEntity.class);
            verify(jpa).save(captor.capture());
            AssessmentEntity entity = captor.getValue();

            assertThat(entity.getId()).isEqualTo(ASSESSMENT_ID.value());
            assertThat(entity.getGroupId()).isEqualTo(GROUP_ID.value());
            assertThat(entity.getEvaluationPeriodId()).isEqualTo(EVAL_PERIOD_ID.value());
            assertThat(entity.getName()).isEqualTo("Math Quiz 1");
            assertThat(entity.getType()).isEqualTo("QUIZ");
            assertThat(entity.getWeight()).isEqualByComparingTo("15.00");
            assertThat(entity.getMaxScore()).isEqualByComparingTo("100.00");
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_NOW);
        }

        @Test
        @DisplayName("should save assessment with null evaluation period")
        void shouldSaveWithNullEvaluationPeriod() {
            Assessment assessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Homework", AssessmentType.WORK,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW
            );

            adapter.save(assessment);

            ArgumentCaptor<AssessmentEntity> captor = ArgumentCaptor.forClass(AssessmentEntity.class);
            verify(jpa).save(captor.capture());
            assertThat(captor.getValue().getEvaluationPeriodId()).isNull();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("should find by ID and map to domain")
        void shouldFindByIdAndMapToDomain() {
            AssessmentEntity entity = createSampleEntity();
            when(jpa.findById(ASSESSMENT_ID.value())).thenReturn(Optional.of(entity));

            Optional<Assessment> result = adapter.findById(ASSESSMENT_ID);

            assertThat(result).isPresent();
            Assessment domain = result.get();
            assertThat(domain.getId()).isEqualTo(ASSESSMENT_ID);
            assertThat(domain.getType()).isEqualTo(AssessmentType.QUIZ);
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(jpa.findById("nonexistent")).thenReturn(Optional.empty());

            Optional<Assessment> result = adapter.findById(new AssessmentId("nonexistent"));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByGroupId")
    class FindByGroupIdTests {

        @Test
        @DisplayName("should find by group ID")
        void shouldFindByGroupId() {
            AssessmentEntity entity = createSampleEntity();
            when(jpa.findByGroupId(GROUP_ID.value())).thenReturn(List.of(entity));

            List<Assessment> results = adapter.findByGroupId(GROUP_ID);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getGroupId()).isEqualTo(GROUP_ID);
        }
    }

    @Nested
    @DisplayName("existsByGroupIdAndName")
    class ExistsByGroupIdAndNameTests {

        @Test
        @DisplayName("should return true when name exists in group")
        void shouldReturnTrueWhenExists() {
            when(jpa.existsByGroupIdAndName(GROUP_ID.value(), "Math Quiz 1")).thenReturn(true);

            boolean exists = adapter.existsByGroupIdAndName(GROUP_ID, "Math Quiz 1");

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("should return false when name does not exist in group")
        void shouldReturnFalseWhenNotExists() {
            when(jpa.existsByGroupIdAndName(GROUP_ID.value(), "Nonexistent")).thenReturn(false);

            boolean exists = adapter.existsByGroupIdAndName(GROUP_ID, "Nonexistent");

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {

        @Test
        @DisplayName("should delete entity via JPA")
        void shouldDeleteViaJpa() {
            Assessment assessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Test", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW
            );

            adapter.delete(assessment);

            ArgumentCaptor<AssessmentEntity> captor = ArgumentCaptor.forClass(AssessmentEntity.class);
            verify(jpa).delete(captor.capture());
            assertThat(captor.getValue().getId()).isEqualTo(ASSESSMENT_ID.value());
        }
    }

    // ======================== helpers ========================

    private AssessmentEntity createSampleEntity() {
        AssessmentEntity entity = new AssessmentEntity();
        entity.setId(ASSESSMENT_ID.value());
        entity.setGroupId(GROUP_ID.value());
        entity.setEvaluationPeriodId(EVAL_PERIOD_ID.value());
        entity.setName("Math Quiz 1");
        entity.setType("QUIZ");
        entity.setWeight(new BigDecimal("15.00"));
        entity.setMaxScore(new BigDecimal("100.00"));
        entity.setCreatedAt(FIXED_NOW);
        entity.setUpdatedAt(FIXED_NOW);
        return entity;
    }
}
