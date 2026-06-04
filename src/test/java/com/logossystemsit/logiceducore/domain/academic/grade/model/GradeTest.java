package com.logossystemsit.logiceducore.domain.academic.grade.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Grade domain")
class GradeTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final GradeId GRADE_ID = GradeId.generate();
    private static final AssessmentId ASSESSMENT_ID = AssessmentId.generate();
    private static final UserId STUDENT_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final BigDecimal VALUE = new BigDecimal("8.50");

    // ======================== GradeId ========================

    @Nested
    @DisplayName("GradeId")
    class GradeIdTests {

        @Test
        @DisplayName("should create valid GradeId")
        void shouldCreateValidGradeId() {
            GradeId id = new GradeId("123e4567-e89b-12d3-a456-426614174000");
            assertThat(id.value()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("should reject null or blank GradeId")
        void shouldRejectNullOrBlankGradeId(String value) {
            assertThatThrownBy(() -> new GradeId(value))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("GradeId");
        }

        @Test
        @DisplayName("should generate non-empty GradeId")
        void shouldGenerateNonEmptyGradeId() {
            assertThat(GradeId.generate().value()).isNotBlank();
        }

        @Test
        @DisplayName("should generate unique GradeIds")
        void shouldGenerateUniqueGradeIds() {
            assertThat(GradeId.generate().value())
                    .isNotEqualTo(GradeId.generate().value());
        }
    }

    // ======================== Grade.create ========================

    @Nested
    @DisplayName("Grade.create")
    class GradeCreateTests {

        @Test
        @DisplayName("should create grade with valid data")
        void shouldCreateWithValidData() {
            Grade grade = Grade.create(
                    GRADE_ID, ASSESSMENT_ID, STUDENT_ID, VALUE, FIXED_NOW
            );

            assertThat(grade.getId()).isEqualTo(GRADE_ID);
            assertThat(grade.getAssessmentId()).isEqualTo(ASSESSMENT_ID);
            assertThat(grade.getStudentId()).isEqualTo(STUDENT_ID);
            assertThat(grade.getValue()).isEqualByComparingTo(VALUE);
            assertThat(grade.getGradedAt()).isEqualTo(FIXED_NOW);
            assertThat(grade.getUpdatedAt()).isEqualTo(FIXED_NOW);
        }

        @Test
        @DisplayName("should create grade with zero value")
        void shouldCreateWithZeroValue() {
            Grade grade = Grade.create(
                    GRADE_ID, ASSESSMENT_ID, STUDENT_ID, BigDecimal.ZERO, FIXED_NOW
            );

            assertThat(grade.getValue()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should create grade with max decimal value")
        void shouldCreateWithMaxDecimalValue() {
            BigDecimal max = new BigDecimal("100.00");
            Grade grade = Grade.create(
                    GRADE_ID, ASSESSMENT_ID, STUDENT_ID, max, FIXED_NOW
            );

            assertThat(grade.getValue()).isEqualByComparingTo(max);
        }

        @Test
        @DisplayName("should reject null GradeId")
        void shouldRejectNullId() {
            assertThatThrownBy(() -> Grade.create(
                    null, ASSESSMENT_ID, STUDENT_ID, VALUE, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("id");
        }

        @Test
        @DisplayName("should reject null AssessmentId")
        void shouldRejectNullAssessmentId() {
            assertThatThrownBy(() -> Grade.create(
                    GRADE_ID, null, STUDENT_ID, VALUE, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("assessmentId");
        }

        @Test
        @DisplayName("should reject null StudentId")
        void shouldRejectNullStudentId() {
            assertThatThrownBy(() -> Grade.create(
                    GRADE_ID, ASSESSMENT_ID, null, VALUE, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("studentId");
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValue() {
            assertThatThrownBy(() -> Grade.create(
                    GRADE_ID, ASSESSMENT_ID, STUDENT_ID, null, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value");
        }

        @Test
        @DisplayName("should reject negative value")
        void shouldRejectNegativeValue() {
            assertThatThrownBy(() -> Grade.create(
                    GRADE_ID, ASSESSMENT_ID, STUDENT_ID, new BigDecimal("-1.00"), FIXED_NOW
            ))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("negative");
        }

        @Test
        @DisplayName("should reject null gradedAt")
        void shouldRejectNullGradedAt() {
            assertThatThrownBy(() -> Grade.create(
                    GRADE_ID, ASSESSMENT_ID, STUDENT_ID, VALUE, null
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("gradedAt");
        }
    }

    // ======================== Grade.restore ========================

    @Nested
    @DisplayName("Grade.restore")
    class GradeRestoreTests {

        @Test
        @DisplayName("should restore grade from persistence")
        void shouldRestoreFromPersistence() {
            Grade grade = Grade.restore(
                    GRADE_ID, ASSESSMENT_ID, STUDENT_ID, VALUE,
                    FIXED_NOW, LATER
            );

            assertThat(grade.getId()).isEqualTo(GRADE_ID);
            assertThat(grade.getValue()).isEqualByComparingTo(VALUE);
            assertThat(grade.getGradedAt()).isEqualTo(FIXED_NOW);
            assertThat(grade.getUpdatedAt()).isEqualTo(LATER);
        }
    }

    // ======================== Grade.changeValue ========================

    @Nested
    @DisplayName("Grade.changeValue")
    class GradeChangeValueTests {

        @Test
        @DisplayName("should change value and update timestamp")
        void shouldChangeValueAndUpdateTimestamp() {
            Grade grade = createSampleGrade();
            BigDecimal newValue = new BigDecimal("9.00");
            Grade changed = grade.changeValue(newValue, LATER);

            assertThat(changed.getValue()).isEqualByComparingTo(newValue);
            assertThat(changed.getUpdatedAt()).isEqualTo(LATER);
            assertThat(changed.getId()).isEqualTo(grade.getId());
            assertThat(changed.getAssessmentId()).isEqualTo(grade.getAssessmentId());
            assertThat(changed.getStudentId()).isEqualTo(grade.getStudentId());
            assertThat(changed.getGradedAt()).isEqualTo(grade.getGradedAt());
        }

        @Test
        @DisplayName("should accept changing to same value")
        void shouldAcceptChangingToSameValue() {
            Grade grade = createSampleGrade();
            Grade changed = grade.changeValue(VALUE, LATER);

            assertThat(changed.getValue()).isEqualByComparingTo(VALUE);
            assertThat(changed.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValueOnChange() {
            Grade grade = createSampleGrade();
            assertThatThrownBy(() -> grade.changeValue(null, LATER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value");
        }

        @Test
        @DisplayName("should reject negative value on change")
        void shouldRejectNegativeValueOnChange() {
            Grade grade = createSampleGrade();
            assertThatThrownBy(() -> grade.changeValue(new BigDecimal("-0.01"), LATER))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("negative");
        }

        @Test
        @DisplayName("should reject null updatedAt on change")
        void shouldRejectNullUpdatedAtOnChange() {
            Grade grade = createSampleGrade();
            assertThatThrownBy(() -> grade.changeValue(VALUE, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("updatedAt");
        }
    }

    // ======================== Grade equality ========================

    @Nested
    @DisplayName("Grade equality")
    class GradeEqualityTests {

        @Test
        @DisplayName("should equal when same ID")
        void shouldEqualWhenSameId() {
            Grade g1 = createSampleGrade();
            Grade g2 = Grade.restore(
                    g1.getId(), g1.getAssessmentId(), g1.getStudentId(), g1.getValue(),
                    g1.getGradedAt(), g1.getUpdatedAt()
            );
            assertThat(g1).isEqualTo(g2);
        }

        @Test
        @DisplayName("should not equal when different ID")
        void shouldNotEqualWhenDifferentId() {
            Grade g1 = createSampleGrade();
            Grade g2 = Grade.create(
                    GradeId.generate(), g1.getAssessmentId(), g1.getStudentId(),
                    g1.getValue(), FIXED_NOW
            );
            assertThat(g1).isNotEqualTo(g2);
        }
    }

    // ======================== helpers ========================

    private Grade createSampleGrade() {
        return Grade.create(
                GRADE_ID, ASSESSMENT_ID, STUDENT_ID, VALUE, FIXED_NOW
        );
    }
}
