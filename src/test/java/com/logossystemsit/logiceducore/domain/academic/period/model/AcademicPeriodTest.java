package com.logossystemsit.logiceducore.domain.academic.period.model;

import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AcademicPeriod domain aggregate")
class AcademicPeriodTest {

    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final LocalDate START_DATE = LocalDate.of(2026, 3, 1);
    private static final LocalDate END_DATE = LocalDate.of(2026, 7, 31);

    @Nested
    @DisplayName("AcademicPeriodId value object")
    class AcademicPeriodIdTest {

        @Test
        @DisplayName("should generate a non-blank UUID")
        void shouldGenerateNonBlankUuid() {
            AcademicPeriodId id = AcademicPeriodId.generate();
            assertThat(id.value()).isNotBlank();
            assertThat(id.value()).containsPattern(
                    "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValue() {
            assertThatThrownBy(() -> new AcademicPeriodId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
        }

        @Test
        @DisplayName("should reject blank value")
        void shouldRejectBlankValue() {
            assertThatThrownBy(() -> new AcademicPeriodId("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
        }
    }

    @Nested
    @DisplayName("PeriodType enum")
    class PeriodTypeTest {

        @Test
        @DisplayName("should contain all expected types")
        void shouldContainAllExpectedTypes() {
            assertThat(PeriodType.values()).containsExactly(
                    PeriodType.SEMESTER,
                    PeriodType.TRIMESTER,
                    PeriodType.CYCLE,
                    PeriodType.BIMESTER,
                    PeriodType.ANUAL,
                    PeriodType.MODULO
            );
        }
    }

    @Nested
    @DisplayName("PeriodStatus enum")
    class PeriodStatusTest {

        @Test
        @DisplayName("should contain ACTIVE and INACTIVE")
        void shouldContainActiveAndInactive() {
            assertThat(PeriodStatus.values()).containsExactly(
                    PeriodStatus.ACTIVE,
                    PeriodStatus.INACTIVE
            );
        }
    }

    @Nested
    @DisplayName("create factory")
    class CreateFactory {

        @Test
        @DisplayName("should create an active period with all fields")
        void shouldCreateActivePeriod() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            assertThat(period.getStatus()).isEqualTo(PeriodStatus.ACTIVE);
            assertThat(period.getLevelId()).isEqualTo(LEVEL_ID);
            assertThat(period.getPeriodType()).isEqualTo(PeriodType.SEMESTER);
            assertThat(period.getName()).isEqualTo("Primer Semestre");
            assertThat(period.getSequence()).isEqualTo(1);
            assertThat(period.getStartDate()).isEqualTo(START_DATE);
            assertThat(period.getEndDate()).isEqualTo(END_DATE);
            assertThat(period.getCreatedAt()).isEqualTo(NOW);
            assertThat(period.getUpdatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("should create period with different data")
        void shouldCreatePeriodWithDifferentData() {
            LocalDate start = LocalDate.of(2026, 8, 1);
            LocalDate end = LocalDate.of(2026, 11, 30);
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.TRIMESTER,
                    "Segundo Trimestre",
                    2,
                    start,
                    end,
                    NOW
            );

            assertThat(period.getName()).isEqualTo("Segundo Trimestre");
            assertThat(period.getPeriodType()).isEqualTo(PeriodType.TRIMESTER);
            assertThat(period.getSequence()).isEqualTo(2);
            assertThat(period.getStartDate()).isEqualTo(start);
            assertThat(period.getEndDate()).isEqualTo(end);
            assertThat(period.getStatus()).isEqualTo(PeriodStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("restore factory")
    class RestoreFactory {

        @Test
        @DisplayName("should restore with explicit state")
        void shouldRestoreWithExplicitState() {
            Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
            AcademicPeriodId id = new AcademicPeriodId("per-001");

            AcademicPeriod period = AcademicPeriod.restore(
                    id,
                    LEVEL_ID,
                    PeriodType.ANUAL,
                    "Ciclo Anual",
                    1,
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31),
                    PeriodStatus.INACTIVE,
                    createdAt,
                    NOW
            );

            assertThat(period.getId()).isEqualTo(id);
            assertThat(period.getLevelId()).isEqualTo(LEVEL_ID);
            assertThat(period.getPeriodType()).isEqualTo(PeriodType.ANUAL);
            assertThat(period.getName()).isEqualTo("Ciclo Anual");
            assertThat(period.getSequence()).isEqualTo(1);
            assertThat(period.getStatus()).isEqualTo(PeriodStatus.INACTIVE);
            assertThat(period.getCreatedAt()).isEqualTo(createdAt);
            assertThat(period.getUpdatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("changeDates behavior")
    class ChangeDatesBehavior {

        @Test
        @DisplayName("should return new instance with updated dates")
        void shouldReturnNewInstanceWithUpdatedDates() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            LocalDate newStart = LocalDate.of(2026, 4, 1);
            LocalDate newEnd = LocalDate.of(2026, 8, 15);
            Instant later = NOW.plusSeconds(3600);
            AcademicPeriod updated = period.changeDates(newStart, newEnd, later);

            assertThat(updated).isNotSameAs(period);
            assertThat(updated.getStartDate()).isEqualTo(newStart);
            assertThat(updated.getEndDate()).isEqualTo(newEnd);
            assertThat(updated.getName()).isEqualTo(period.getName());
            assertThat(updated.getStatus()).isEqualTo(period.getStatus());
            assertThat(updated.getId()).isEqualTo(period.getId());
            assertThat(updated.getLevelId()).isEqualTo(period.getLevelId());
            assertThat(updated.getUpdatedAt()).isEqualTo(later);
        }

        @Test
        @DisplayName("should reject start date after end date")
        void shouldRejectStartDateAfterEndDate() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            assertThatThrownBy(() -> period.changeDates(
                    LocalDate.of(2026, 8, 1),
                    LocalDate.of(2026, 1, 1),
                    NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("start date must be before end date");
        }

        @Test
        @DisplayName("should reject end date before start date")
        void shouldRejectEndDateBeforeStartDate() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            assertThatThrownBy(() -> period.changeDates(
                    LocalDate.of(2026, 12, 1),
                    LocalDate.of(2026, 11, 1),
                    NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("start date must be before end date");
        }
    }

    @Nested
    @DisplayName("changeName behavior")
    class ChangeNameBehavior {

        @Test
        @DisplayName("should return new instance with updated name")
        void shouldReturnNewInstanceWithUpdatedName() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            Instant later = NOW.plusSeconds(3600);
            AcademicPeriod updated = period.changeName("Semestre 2026-I", later);

            assertThat(updated).isNotSameAs(period);
            assertThat(updated.getName()).isEqualTo("Semestre 2026-I");
            assertThat(updated.getStatus()).isEqualTo(period.getStatus());
            assertThat(updated.getId()).isEqualTo(period.getId());
            assertThat(updated.getUpdatedAt()).isEqualTo(later);
        }

        @Test
        @DisplayName("should reject null name")
        void shouldRejectNullName() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            assertThatThrownBy(() -> period.changeName(null, NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject blank name")
        void shouldRejectBlankName() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            assertThatThrownBy(() -> period.changeName("  ", NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");
        }
    }

    @Nested
    @DisplayName("deactivate behavior")
    class DeactivateBehavior {

        @Test
        @DisplayName("should set status to INACTIVE")
        void shouldSetStatusToInactive() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            AcademicPeriod deactivated = period.deactivate(NOW.plusSeconds(3600));

            assertThat(deactivated.getStatus()).isEqualTo(PeriodStatus.INACTIVE);
            assertThat(deactivated.getId()).isEqualTo(period.getId());
            assertThat(deactivated.getName()).isEqualTo(period.getName());
            assertThat(deactivated.getStartDate()).isEqualTo(period.getStartDate());
            assertThat(deactivated.getEndDate()).isEqualTo(period.getEndDate());
        }

        @Test
        @DisplayName("should return same state if already inactive")
        void shouldReturnSameStateIfAlreadyInactive() {
            AcademicPeriod inactive = AcademicPeriod.restore(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    PeriodStatus.INACTIVE,
                    NOW,
                    NOW
            );

            AcademicPeriod result = inactive.deactivate(NOW.plusSeconds(3600));

            assertThat(result.getStatus()).isEqualTo(PeriodStatus.INACTIVE);
            assertThat(result).isEqualTo(inactive);
        }
    }

    @Nested
    @DisplayName("immutability")
    class Immutability {

        @Test
        @DisplayName("should have no public setters")
        void shouldHaveNoPublicSetters() {
            var methods = AcademicPeriod.class.getMethods();
            for (var method : methods) {
                assertThat(method.getName()).doesNotStartWith("set");
            }
        }

        @Test
        @DisplayName("changeDates produces a different instance")
        void changeDatesProducesDifferentInstance() {
            AcademicPeriod period = AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            );

            AcademicPeriod updated = period.changeDates(
                    LocalDate.of(2026, 4, 1),
                    LocalDate.of(2026, 8, 15),
                    NOW.plusSeconds(3600)
            );

            assertThat(updated).isNotSameAs(period);
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should reject null PeriodType")
        void shouldRejectNullPeriodType() {
            assertThatThrownBy(() -> AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    null,
                    "Primer Semestre",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            )).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should reject null name")
        void shouldRejectNullNameInCreate() {
            assertThatThrownBy(() -> AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    null,
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            )).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject blank name")
        void shouldRejectBlankNameInCreate() {
            assertThatThrownBy(() -> AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "  ",
                    1,
                    START_DATE,
                    END_DATE,
                    NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject start date after end date in create")
        void shouldRejectStartDateAfterEndDateInCreate() {
            assertThatThrownBy(() -> AcademicPeriod.create(
                    AcademicPeriodId.generate(),
                    LEVEL_ID,
                    PeriodType.SEMESTER,
                    "Primer Semestre",
                    1,
                    LocalDate.of(2026, 8, 1),
                    LocalDate.of(2026, 1, 1),
                    NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("start date must be before end date");
        }
    }
}
