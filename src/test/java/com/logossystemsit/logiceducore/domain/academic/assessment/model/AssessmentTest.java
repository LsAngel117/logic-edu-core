package com.logossystemsit.logiceducore.domain.academic.assessment.model;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentType;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Assessment domain")
class AssessmentTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final AssessmentId ASSESSMENT_ID = AssessmentId.generate();
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final EvaluationPeriodId EVALUATION_PERIOD_ID = EvaluationPeriodId.generate();

    // ======================== AssessmentId ========================

    @Nested
    @DisplayName("AssessmentId")
    class AssessmentIdTests {

        @Test
        @DisplayName("should create valid AssessmentId")
        void shouldCreateValidAssessmentId() {
            AssessmentId id = new AssessmentId("123e4567-e89b-12d3-a456-426614174000");
            assertThat(id.value()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("should reject null or blank AssessmentId")
        void shouldRejectNullOrBlankAssessmentId(String value) {
            assertThatThrownBy(() -> new AssessmentId(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("AssessmentId");
        }

        @Test
        @DisplayName("should generate non-empty AssessmentId")
        void shouldGenerateNonEmptyAssessmentId() {
            assertThat(AssessmentId.generate().value()).isNotBlank();
        }

        @Test
        @DisplayName("should generate unique AssessmentIds")
        void shouldGenerateUniqueAssessmentIds() {
            assertThat(AssessmentId.generate().value())
                    .isNotEqualTo(AssessmentId.generate().value());
        }
    }

    // ======================== AssessmentType ========================

    @Nested
    @DisplayName("AssessmentType")
    class AssessmentTypeTests {

        @Test
        @DisplayName("should have four expected values")
        void shouldHaveFourExpectedValues() {
            assertThat(AssessmentType.values()).containsExactly(
                    AssessmentType.QUIZ,
                    AssessmentType.EXAM,
                    AssessmentType.WORK,
                    AssessmentType.PARTICIPATION
            );
        }

        @ParameterizedTest
        @EnumSource(AssessmentType.class)
        @DisplayName("should resolve all enum values")
        void shouldResolveAllEnumValues(AssessmentType type) {
            assertThat(AssessmentType.valueOf(type.name())).isEqualTo(type);
        }
    }

    // ======================== Assessment.create ========================

    @Nested
    @DisplayName("Assessment.create")
    class AssessmentCreateTests {

        @Test
        @DisplayName("should create assessment with valid data")
        void shouldCreateWithValidData() {
            Assessment assessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, EVALUATION_PERIOD_ID,
                    "Math Quiz 1", AssessmentType.QUIZ,
                    new BigDecimal("15.00"), new BigDecimal("100.00"),
                    FIXED_NOW
            );

            assertThat(assessment.getId()).isEqualTo(ASSESSMENT_ID);
            assertThat(assessment.getGroupId()).isEqualTo(GROUP_ID);
            assertThat(assessment.getEvaluationPeriodId()).contains(EVALUATION_PERIOD_ID);
            assertThat(assessment.getName()).isEqualTo("Math Quiz 1");
            assertThat(assessment.getType()).isEqualTo(AssessmentType.QUIZ);
            assertThat(assessment.getWeight()).isEqualByComparingTo("15.00");
            assertThat(assessment.getMaxScore()).isEqualByComparingTo("100.00");
            assertThat(assessment.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(assessment.getUpdatedAt()).isEqualTo(FIXED_NOW);
        }

        @Test
        @DisplayName("should create assessment without evaluation period")
        void shouldCreateWithoutEvaluationPeriod() {
            Assessment assessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Participation", AssessmentType.PARTICIPATION,
                    new BigDecimal("10.00"), new BigDecimal("50.00"),
                    FIXED_NOW
            );

            assertThat(assessment.getEvaluationPeriodId()).isEmpty();
            assertThat(assessment.getType()).isEqualTo(AssessmentType.PARTICIPATION);
        }

        @Test
        @DisplayName("should create assessment with EXAM type")
        void shouldCreateExamAssessment() {
            Assessment assessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Final Exam", AssessmentType.EXAM,
                    new BigDecimal("30.00"), new BigDecimal("100.00"),
                    FIXED_NOW
            );

            assertThat(assessment.getType()).isEqualTo(AssessmentType.EXAM);
            assertThat(assessment.getWeight()).isEqualByComparingTo("30.00");
        }

        @Test
        @DisplayName("should create assessment with WORK type")
        void shouldCreateWorkAssessment() {
            Assessment assessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Homework 1", AssessmentType.WORK,
                    new BigDecimal("20.00"), new BigDecimal("50.00"),
                    FIXED_NOW
            );

            assertThat(assessment.getType()).isEqualTo(AssessmentType.WORK);
        }

        @Test
        @DisplayName("should reject null id")
        void shouldRejectNullId() {
            assertThatThrownBy(() -> Assessment.create(
                    null, GROUP_ID, null,
                    "Test", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("id");
        }

        @Test
        @DisplayName("should reject null groupId")
        void shouldRejectNullGroupId() {
            assertThatThrownBy(() -> Assessment.create(
                    ASSESSMENT_ID, null, null,
                    "Test", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("groupId");
        }

        @Test
        @DisplayName("should reject null or blank name")
        void shouldRejectNullOrBlankName() {
            assertThatThrownBy(() -> Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    null, AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");

            assertThatThrownBy(() -> Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "  ", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject null type")
        void shouldRejectNullType() {
            assertThatThrownBy(() -> Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Test", null,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("type");
        }

        @Test
        @DisplayName("should reject weight <= 0")
        void shouldRejectInvalidWeight() {
            assertThatThrownBy(() -> Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Test", AssessmentType.QUIZ,
                    BigDecimal.ZERO, BigDecimal.TEN,
                    FIXED_NOW
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");

            assertThatThrownBy(() -> Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Test", AssessmentType.QUIZ,
                    new BigDecimal("-1"), BigDecimal.TEN,
                    FIXED_NOW
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
        }

        @Test
        @DisplayName("should reject maxScore <= 0")
        void shouldRejectInvalidMaxScore() {
            assertThatThrownBy(() -> Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Test", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.ZERO,
                    FIXED_NOW
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("maxScore");

            assertThatThrownBy(() -> Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Test", AssessmentType.QUIZ,
                    BigDecimal.ONE, new BigDecimal("-5"),
                    FIXED_NOW
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("maxScore");
        }

        @Test
        @DisplayName("should trim name")
        void shouldTrimName() {
            Assessment assessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "  Math Quiz 1  ", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW
            );

            assertThat(assessment.getName()).isEqualTo("Math Quiz 1");
        }
    }

    // ======================== Assessment.restore ========================

    @Nested
    @DisplayName("Assessment.restore")
    class AssessmentRestoreTests {

        @Test
        @DisplayName("should restore assessment from persistence")
        void shouldRestoreFromPersistence() {
            Assessment assessment = Assessment.restore(
                    ASSESSMENT_ID, GROUP_ID, EVALUATION_PERIOD_ID,
                    "Final Exam", AssessmentType.EXAM,
                    new BigDecimal("25.00"), new BigDecimal("100.00"),
                    FIXED_NOW, LATER
            );

            assertThat(assessment.getId()).isEqualTo(ASSESSMENT_ID);
            assertThat(assessment.getGroupId()).isEqualTo(GROUP_ID);
            assertThat(assessment.getEvaluationPeriodId()).contains(EVALUATION_PERIOD_ID);
            assertThat(assessment.getName()).isEqualTo("Final Exam");
            assertThat(assessment.getType()).isEqualTo(AssessmentType.EXAM);
            assertThat(assessment.getWeight()).isEqualByComparingTo("25.00");
            assertThat(assessment.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(assessment.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("should restore with null evaluation period")
        void shouldRestoreWithNullEvaluationPeriod() {
            Assessment assessment = Assessment.restore(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Homework", AssessmentType.WORK,
                    BigDecimal.ONE, BigDecimal.TEN,
                    FIXED_NOW, FIXED_NOW
            );

            assertThat(assessment.getEvaluationPeriodId()).isEmpty();
        }
    }

    // ======================== Assessment.changeData ========================

    @Nested
    @DisplayName("Assessment.changeData")
    class AssessmentChangeDataTests {

        @Test
        @DisplayName("should change name and type")
        void shouldChangeNameAndType() {
            Assessment assessment = createSampleAssessment();
            Assessment changed = assessment.changeData(
                    "Updated Quiz", AssessmentType.EXAM,
                    new BigDecimal("15.00"), new BigDecimal("100.00"),
                    EVALUATION_PERIOD_ID, LATER
            );

            assertThat(changed.getName()).isEqualTo("Updated Quiz");
            assertThat(changed.getType()).isEqualTo(AssessmentType.EXAM);
            assertThat(changed.getUpdatedAt()).isEqualTo(LATER);
            assertThat(changed.getId()).isEqualTo(assessment.getId());
            assertThat(changed.getCreatedAt()).isEqualTo(assessment.getCreatedAt());
        }

        @Test
        @DisplayName("should change evaluation period to another value")
        void shouldChangeEvaluationPeriod() {
            Assessment assessment = createSampleAssessment();
            EvaluationPeriodId newPeriodId = EvaluationPeriodId.generate();
            Assessment changed = assessment.changeData(
                    "Math Quiz 1", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    newPeriodId, LATER
            );

            assertThat(changed.getEvaluationPeriodId()).contains(newPeriodId);
        }

        @Test
        @DisplayName("should set evaluation period to null")
        void shouldSetEvaluationPeriodToNull() {
            Assessment assessment = createSampleAssessment();
            Assessment changed = assessment.changeData(
                    "Math Quiz 1", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    null, LATER
            );

            assertThat(changed.getEvaluationPeriodId()).isEmpty();
        }

        @Test
        @DisplayName("should change weight and maxScore")
        void shouldChangeWeightAndMaxScore() {
            Assessment assessment = createSampleAssessment();
            Assessment changed = assessment.changeData(
                    "Math Quiz 1", AssessmentType.QUIZ,
                    new BigDecimal("25.00"), new BigDecimal("200.00"),
                    null, LATER
            );

            assertThat(changed.getWeight()).isEqualByComparingTo("25.00");
            assertThat(changed.getMaxScore()).isEqualByComparingTo("200.00");
        }

        @Test
        @DisplayName("should reject weight <= 0 on change")
        void shouldRejectInvalidWeightOnChange() {
            Assessment assessment = createSampleAssessment();
            assertThatThrownBy(() -> assessment.changeData(
                    "Test", AssessmentType.QUIZ,
                    BigDecimal.ZERO, BigDecimal.TEN,
                    null, LATER
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
        }

        @Test
        @DisplayName("should reject maxScore <= 0 on change")
        void shouldRejectInvalidMaxScoreOnChange() {
            Assessment assessment = createSampleAssessment();
            assertThatThrownBy(() -> assessment.changeData(
                    "Test", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.ZERO,
                    null, LATER
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("maxScore");
        }

        @Test
        @DisplayName("should reject null name on change")
        void shouldRejectNullNameOnChange() {
            Assessment assessment = createSampleAssessment();
            assertThatThrownBy(() -> assessment.changeData(
                    null, AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    null, LATER
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject null type on change")
        void shouldRejectNullTypeOnChange() {
            Assessment assessment = createSampleAssessment();
            assertThatThrownBy(() -> assessment.changeData(
                    "Test", null,
                    BigDecimal.ONE, BigDecimal.TEN,
                    null, LATER
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("type");
        }

        @Test
        @DisplayName("should trim name on change")
        void shouldTrimNameOnChange() {
            Assessment assessment = createSampleAssessment();
            Assessment changed = assessment.changeData(
                    "  Updated  ", AssessmentType.QUIZ,
                    BigDecimal.ONE, BigDecimal.TEN,
                    null, LATER
            );

            assertThat(changed.getName()).isEqualTo("Updated");
        }
    }

    // ======================== Assessment equality ========================

    @Nested
    @DisplayName("Assessment equality")
    class AssessmentEqualityTests {

        @Test
        @DisplayName("should equal when same ID")
        void shouldEqualWhenSameId() {
            Assessment a1 = createSampleAssessment();
            Assessment a2 = Assessment.restore(
                    a1.getId(), a1.getGroupId(), a1.getEvaluationPeriodId().orElse(null),
                    a1.getName(), a1.getType(),
                    a1.getWeight(), a1.getMaxScore(),
                    a1.getCreatedAt(), a1.getUpdatedAt()
            );
            assertThat(a1).isEqualTo(a2);
        }

        @Test
        @DisplayName("should not equal when different ID")
        void shouldNotEqualWhenDifferentId() {
            Assessment a1 = createSampleAssessment();
            Assessment a2 = Assessment.create(
                    AssessmentId.generate(), a1.getGroupId(), a1.getEvaluationPeriodId().orElse(null),
                    a1.getName(), a1.getType(),
                    a1.getWeight(), a1.getMaxScore(),
                    FIXED_NOW
            );
            assertThat(a1).isNotEqualTo(a2);
        }
    }

    // ======================== helpers ========================

    private Assessment createSampleAssessment() {
        return Assessment.create(
                ASSESSMENT_ID, GROUP_ID, EVALUATION_PERIOD_ID,
                "Math Quiz 1", AssessmentType.QUIZ,
                BigDecimal.ONE, BigDecimal.TEN,
                FIXED_NOW
        );
    }
}
