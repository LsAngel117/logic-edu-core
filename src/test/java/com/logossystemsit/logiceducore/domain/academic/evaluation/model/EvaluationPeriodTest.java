package com.logossystemsit.logiceducore.domain.academic.evaluation.model;

import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EvaluationPeriod domain aggregate")
class EvaluationPeriodTest {

    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("990e8400-e29b-41d4-a716-446655440004");
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final LocalDate START_DATE = LocalDate.of(2026, 3, 1);
    private static final LocalDate END_DATE = LocalDate.of(2026, 4, 15);

    @Nested
    @DisplayName("EvaluationPeriodId value object")
    class EvaluationPeriodIdTest {

        @Test
        @DisplayName("should generate a non-blank UUID")
        void shouldGenerateNonBlankUuid() {
            EvaluationPeriodId id = EvaluationPeriodId.generate();
            assertThat(id.value()).isNotBlank();
            assertThat(id.value()).containsPattern(
                    "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValue() {
            assertThatThrownBy(() -> new EvaluationPeriodId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
        }

        @Test
        @DisplayName("should reject blank value")
        void shouldRejectBlankValue() {
            assertThatThrownBy(() -> new EvaluationPeriodId("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
        }
    }

    @Nested
    @DisplayName("EvaluationPeriodStatus enum")
    class EvaluationPeriodStatusTest {

        @Test
        @DisplayName("should contain ACTIVE and INACTIVE")
        void shouldContainActiveAndInactive() {
            assertThat(EvaluationPeriodStatus.values()).containsExactly(
                    EvaluationPeriodStatus.ACTIVE,
                    EvaluationPeriodStatus.INACTIVE
            );
        }
    }

    @Nested
    @DisplayName("create factory")
    class Create {

        @Test
        @DisplayName("should create active evaluation period with valid data")
        void shouldCreateActivePeriodWithValidData() {
            EvaluationPeriodId id = EvaluationPeriodId.generate();
            EvaluationPeriod ep = EvaluationPeriod.create(
                    id, PERIOD_ID, "Primer Examen", 1,
                    new BigDecimal("25.00"),
                    START_DATE, END_DATE, NOW
            );

            assertThat(ep.getId()).isEqualTo(id);
            assertThat(ep.getPeriodId()).isEqualTo(PERIOD_ID);
            assertThat(ep.getName()).isEqualTo("Primer Examen");
            assertThat(ep.getSequence()).isEqualTo(1);
            assertThat(ep.getWeight()).isEqualByComparingTo(new BigDecimal("25.00"));
            assertThat(ep.getStartDate()).isEqualTo(START_DATE);
            assertThat(ep.getEndDate()).isEqualTo(END_DATE);
            assertThat(ep.getStatus()).isEqualTo(EvaluationPeriodStatus.ACTIVE);
            assertThat(ep.getCreatedAt()).isEqualTo(NOW);
            assertThat(ep.getUpdatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("should accept max weight of 100")
        void shouldAcceptMaxWeight() {
            EvaluationPeriod ep = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Final", 2,
                    new BigDecimal("100.00"),
                    START_DATE, END_DATE, NOW
            );

            assertThat(ep.getWeight()).isEqualByComparingTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("should accept min weight just above 0")
        void shouldAcceptMinWeight() {
            EvaluationPeriod ep = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Tarea", 3,
                    new BigDecimal("0.01"),
                    START_DATE, END_DATE, NOW
            );

            assertThat(ep.getWeight()).isEqualByComparingTo(new BigDecimal("0.01"));
        }
    }

    @Nested
    @DisplayName("weight validation")
    class WeightValidation {

        @Test
        @DisplayName("should reject weight > 100")
        void shouldRejectWeightAbove100() {
            assertThatThrownBy(() -> EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Heavy", 1,
                    new BigDecimal("100.01"),
                    START_DATE, END_DATE, NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
        }

        @Test
        @DisplayName("should reject weight <= 0")
        void shouldRejectWeightZeroOrBelow() {
            assertThatThrownBy(() -> EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Zero", 1,
                    new BigDecimal("0.00"),
                    START_DATE, END_DATE, NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
        }

        @Test
        @DisplayName("should reject negative weight")
        void shouldRejectNegativeWeight() {
            assertThatThrownBy(() -> EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Negative", 1,
                    new BigDecimal("-1.00"),
                    START_DATE, END_DATE, NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
        }
    }

    @Nested
    @DisplayName("date validation")
    class DateValidation {

        @Test
        @DisplayName("should reject startDate after endDate")
        void shouldRejectStartAfterEnd() {
            assertThatThrownBy(() -> EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Bad dates", 1,
                    new BigDecimal("50.00"),
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 4, 1), NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("start date must be before end date");
        }

        @Test
        @DisplayName("should reject equal startDate and endDate")
        void shouldRejectEqualDates() {
            assertThatThrownBy(() -> EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Same day", 1,
                    new BigDecimal("50.00"),
                    START_DATE, START_DATE, NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("start date must be before end date");
        }
    }

    @Nested
    @DisplayName("restore factory")
    class Restore {

        @Test
        @DisplayName("should restore from persistence with all fields")
        void shouldRestoreFromPersistence() {
            EvaluationPeriodId id = new EvaluationPeriodId("test-id");
            Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2025-06-01T00:00:00Z");
            BigDecimal weight = new BigDecimal("33.33");

            EvaluationPeriod ep = EvaluationPeriod.restore(
                    id, PERIOD_ID, "Restored", 2, weight,
                    START_DATE, END_DATE,
                    EvaluationPeriodStatus.ACTIVE,
                    createdAt, updatedAt
            );

            assertThat(ep.getId()).isEqualTo(id);
            assertThat(ep.getPeriodId()).isEqualTo(PERIOD_ID);
            assertThat(ep.getName()).isEqualTo("Restored");
            assertThat(ep.getSequence()).isEqualTo(2);
            assertThat(ep.getWeight()).isEqualByComparingTo(weight);
            assertThat(ep.getStartDate()).isEqualTo(START_DATE);
            assertThat(ep.getEndDate()).isEqualTo(END_DATE);
            assertThat(ep.getStatus()).isEqualTo(EvaluationPeriodStatus.ACTIVE);
            assertThat(ep.getCreatedAt()).isEqualTo(createdAt);
            assertThat(ep.getUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("should restore inactive period")
        void shouldRestoreInactivePeriod() {
            EvaluationPeriod ep = EvaluationPeriod.restore(
                    new EvaluationPeriodId("inactive-id"), PERIOD_ID, "Old", 1,
                    new BigDecimal("50.00"),
                    START_DATE, END_DATE,
                    EvaluationPeriodStatus.INACTIVE,
                    NOW, NOW.plusSeconds(3600)
            );

            assertThat(ep.getStatus()).isEqualTo(EvaluationPeriodStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("changeWeight behavior")
    class ChangeWeight {

        @Test
        @DisplayName("should change weight to a new valid value")
        void shouldChangeWeight() {
            EvaluationPeriod ep = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Eval", 1,
                    new BigDecimal("25.00"),
                    START_DATE, END_DATE, NOW
            );

            EvaluationPeriod updated = ep.changeWeight(new BigDecimal("40.00"), NOW.plusSeconds(3600));

            assertThat(updated.getId()).isEqualTo(ep.getId());
            assertThat(updated.getWeight()).isEqualByComparingTo(new BigDecimal("40.00"));
            assertThat(updated.getUpdatedAt()).isEqualTo(NOW.plusSeconds(3600));
            assertThat(updated.getName()).isEqualTo(ep.getName());
        }

        @Test
        @DisplayName("should reject invalid weight in changeWeight")
        void shouldRejectInvalidWeightChange() {
            EvaluationPeriod ep = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Eval", 1,
                    new BigDecimal("25.00"),
                    START_DATE, END_DATE, NOW
            );

            assertThatThrownBy(() -> ep.changeWeight(new BigDecimal("150.00"), NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
        }
    }

    @Nested
    @DisplayName("changeDates behavior")
    class ChangeDates {

        @Test
        @DisplayName("should change to new valid dates")
        void shouldChangeDates() {
            EvaluationPeriod ep = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Eval", 1,
                    new BigDecimal("25.00"),
                    START_DATE, END_DATE, NOW
            );

            LocalDate newStart = LocalDate.of(2026, 4, 20);
            LocalDate newEnd = LocalDate.of(2026, 5, 10);
            EvaluationPeriod updated = ep.changeDates(newStart, newEnd, NOW.plusSeconds(3600));

            assertThat(updated.getStartDate()).isEqualTo(newStart);
            assertThat(updated.getEndDate()).isEqualTo(newEnd);
            assertThat(updated.getUpdatedAt()).isEqualTo(NOW.plusSeconds(3600));
        }

        @Test
        @DisplayName("should reject invalid date range on change")
        void shouldRejectInvalidDateRangeChange() {
            EvaluationPeriod ep = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Eval", 1,
                    new BigDecimal("25.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 30), NOW
            );

            assertThatThrownBy(() -> ep.changeDates(
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 4, 1), NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("start date must be before end date");
        }
    }

    @Nested
    @DisplayName("deactivate behavior")
    class Deactivate {

        @Test
        @DisplayName("should deactivate an active period")
        void shouldDeactivateActivePeriod() {
            EvaluationPeriod ep = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Eval", 1,
                    new BigDecimal("25.00"),
                    START_DATE, END_DATE, NOW
            );

            EvaluationPeriod deactivated = ep.deactivate(NOW.plusSeconds(7200));

            assertThat(deactivated.getStatus()).isEqualTo(EvaluationPeriodStatus.INACTIVE);
            assertThat(deactivated.getUpdatedAt()).isEqualTo(NOW.plusSeconds(7200));
        }

        @Test
        @DisplayName("should be idempotent when already inactive")
        void shouldBeIdempotentWhenAlreadyInactive() {
            EvaluationPeriod ep = EvaluationPeriod.restore(
                    new EvaluationPeriodId("inactive-id"), PERIOD_ID, "Old", 1,
                    new BigDecimal("25.00"),
                    START_DATE, END_DATE,
                    EvaluationPeriodStatus.INACTIVE,
                    NOW, NOW
            );

            EvaluationPeriod result = ep.deactivate(NOW.plusSeconds(3600));

            assertThat(result.getStatus()).isEqualTo(EvaluationPeriodStatus.INACTIVE);
            assertThat(result).isSameAs(ep);
        }
    }

    @Nested
    @DisplayName("immutability")
    class Immutability {

        @Test
        @DisplayName("create should produce new object each time")
        void shouldProduceNewObject() {
            EvaluationPeriod ep1 = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "A", 1,
                    new BigDecimal("50.00"),
                    START_DATE, END_DATE, NOW
            );
            EvaluationPeriod ep2 = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "B", 2,
                    new BigDecimal("75.00"),
                    START_DATE, END_DATE, NOW
            );

            assertThat(ep1).isNotSameAs(ep2);
            assertThat(ep1.getName()).isNotEqualTo(ep2.getName());
        }

        @Test
        @DisplayName("behavior methods should return new instances")
        void shouldReturnNewInstances() {
            EvaluationPeriod original = EvaluationPeriod.create(
                    EvaluationPeriodId.generate(), PERIOD_ID, "Orig", 1,
                    new BigDecimal("50.00"),
                    START_DATE, END_DATE, NOW
            );

            EvaluationPeriod changed = original.changeWeight(new BigDecimal("75.00"), NOW);
            assertThat(changed).isNotSameAs(original);
            assertThat(original.getWeight()).isEqualByComparingTo(new BigDecimal("50.00"));
        }
    }
}
