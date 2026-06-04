package com.logossystemsit.logiceducore.domain.academic.level.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelStatus;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AcademicLevel domain aggregate")
class AcademicLevelTest {

    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @Nested
    @DisplayName("AcademicLevelId value object")
    class AcademicLevelIdTest {

        @Test
        @DisplayName("should generate a non-blank UUID")
        void shouldGenerateNonBlankUuid() {
            AcademicLevelId id = AcademicLevelId.generate();
            assertThat(id.value()).isNotBlank();
            assertThat(id.value()).containsPattern(
                    "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValue() {
            assertThatThrownBy(() -> new AcademicLevelId(null))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("required");
        }

        @Test
        @DisplayName("should reject blank value")
        void shouldRejectBlankValue() {
            assertThatThrownBy(() -> new AcademicLevelId("  "))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("required");
        }
    }

    @Nested
    @DisplayName("AcademicLevelStatus enum")
    class AcademicLevelStatusTest {

        @Test
        @DisplayName("should contain ACTIVE and INACTIVE")
        void shouldContainActiveAndInactive() {
            assertThat(AcademicLevelStatus.values()).containsExactly(
                    AcademicLevelStatus.ACTIVE,
                    AcademicLevelStatus.INACTIVE
            );
        }
    }

    @Nested
    @DisplayName("create factory")
    class CreateFactory {

        @Test
        @DisplayName("should create an active level")
        void shouldCreateActiveLevel() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    NOW
            );

            assertThat(level.getStatus()).isEqualTo(AcademicLevelStatus.ACTIVE);
            assertThat(level.getSchoolId()).isEqualTo(SCHOOL_ID);
            assertThat(level.getName()).isEqualTo("Primaria");
            assertThat(level.getNumber()).isEqualTo(1);
            assertThat(level.getCreatedAt()).isEqualTo(NOW);
            assertThat(level.getUpdatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("should create level with different data")
        void shouldCreateLevelWithDifferentData() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Secundaria",
                    5,
                    NOW
            );

            assertThat(level.getName()).isEqualTo("Secundaria");
            assertThat(level.getNumber()).isEqualTo(5);
            assertThat(level.getStatus()).isEqualTo(AcademicLevelStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("restore factory")
    class RestoreFactory {

        @Test
        @DisplayName("should restore with explicit state")
        void shouldRestoreWithExplicitState() {
            Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
            AcademicLevelId id = new AcademicLevelId("abc-123");

            AcademicLevel level = AcademicLevel.restore(
                    id,
                    SCHOOL_ID,
                    "Bachillerato",
                    3,
                    AcademicLevelStatus.INACTIVE,
                    createdAt,
                    NOW
            );

            assertThat(level.getId()).isEqualTo(id);
            assertThat(level.getSchoolId()).isEqualTo(SCHOOL_ID);
            assertThat(level.getName()).isEqualTo("Bachillerato");
            assertThat(level.getNumber()).isEqualTo(3);
            assertThat(level.getStatus()).isEqualTo(AcademicLevelStatus.INACTIVE);
            assertThat(level.getCreatedAt()).isEqualTo(createdAt);
            assertThat(level.getUpdatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("changeName behavior")
    class ChangeNameBehavior {

        @Test
        @DisplayName("should return new instance with updated name")
        void shouldReturnNewInstanceWithUpdatedName() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    NOW
            );

            AcademicLevel updated = level.changeName("Educación Primaria", NOW.plusSeconds(3600));

            assertThat(updated).isNotSameAs(level);
            assertThat(updated.getName()).isEqualTo("Educación Primaria");
            assertThat(updated.getNumber()).isEqualTo(level.getNumber());
            assertThat(updated.getStatus()).isEqualTo(level.getStatus());
            assertThat(updated.getId()).isEqualTo(level.getId());
            assertThat(updated.getSchoolId()).isEqualTo(level.getSchoolId());
            assertThat(updated.getUpdatedAt()).isEqualTo(NOW.plusSeconds(3600));
        }

        @Test
        @DisplayName("should reject null name")
        void shouldRejectNullName() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    NOW
            );

            assertThatThrownBy(() -> level.changeName(null, NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject blank name")
        void shouldRejectBlankName() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    NOW
            );

            assertThatThrownBy(() -> level.changeName("  ", NOW))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("name");
        }
    }

    @Nested
    @DisplayName("changeNumber behavior")
    class ChangeNumberBehavior {

        @Test
        @DisplayName("should return new instance with updated number")
        void shouldReturnNewInstanceWithUpdatedNumber() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    NOW
            );

            AcademicLevel updated = level.changeNumber(3, NOW.plusSeconds(3600));

            assertThat(updated).isNotSameAs(level);
            assertThat(updated.getNumber()).isEqualTo(3);
            assertThat(updated.getName()).isEqualTo(level.getName());
            assertThat(updated.getStatus()).isEqualTo(level.getStatus());
            assertThat(updated.getId()).isEqualTo(level.getId());
            assertThat(updated.getSchoolId()).isEqualTo(level.getSchoolId());
        }

        @Test
        @DisplayName("should keep number unchanged when same value passed")
        void shouldKeepNumberUnchangedWhenSameValuePassed() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    NOW
            );

            AcademicLevel updated = level.changeNumber(1, NOW.plusSeconds(3600));

            assertThat(updated.getNumber()).isEqualTo(1);
            assertThat(updated.getId()).isEqualTo(level.getId());
            assertThat(updated.getName()).isEqualTo(level.getName());
            assertThat(updated.getStatus()).isEqualTo(level.getStatus());
            assertThat(updated.getUpdatedAt()).isEqualTo(NOW.plusSeconds(3600));
        }
    }

    @Nested
    @DisplayName("deactivate behavior")
    class DeactivateBehavior {

        @Test
        @DisplayName("should set status to INACTIVE")
        void shouldSetStatusToInactive() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    NOW
            );

            AcademicLevel deactivated = level.deactivate(NOW.plusSeconds(3600));

            assertThat(deactivated.getStatus()).isEqualTo(AcademicLevelStatus.INACTIVE);
            assertThat(deactivated.getId()).isEqualTo(level.getId());
            assertThat(deactivated.getName()).isEqualTo(level.getName());
            assertThat(deactivated.getNumber()).isEqualTo(level.getNumber());
        }

        @Test
        @DisplayName("should return same state if already inactive")
        void shouldReturnSameStateIfAlreadyInactive() {
            AcademicLevel inactive = AcademicLevel.restore(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    AcademicLevelStatus.INACTIVE,
                    NOW,
                    NOW
            );

            AcademicLevel result = inactive.deactivate(NOW.plusSeconds(3600));

            assertThat(result.getStatus()).isEqualTo(AcademicLevelStatus.INACTIVE);
            assertThat(result).isEqualTo(inactive);
        }
    }

    @Nested
    @DisplayName("immutability")
    class Immutability {

        @Test
        @DisplayName("should have no public setters")
        void shouldHaveNoPublicSetters() {
            var methods = AcademicLevel.class.getMethods();
            for (var method : methods) {
                assertThat(method.getName()).doesNotStartWith("set");
            }
        }

        @Test
        @DisplayName("deactivate produces a different instance")
        void deactivateProducesDifferentInstance() {
            AcademicLevel level = AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    1,
                    NOW
            );

            AcademicLevel deactivated = level.deactivate(NOW.plusSeconds(3600));

            assertThat(deactivated).isNotSameAs(level);
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should reject null SchoolId")
        void shouldRejectNullSchoolId() {
            assertThatThrownBy(() -> AcademicLevel.create(
                    AcademicLevelId.generate(),
                    null,
                    "Primaria",
                    1,
                    NOW
            )).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should reject null name")
        void shouldRejectNullNameInCreate() {
            assertThatThrownBy(() -> AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    null,
                    1,
                    NOW
            )).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject blank name")
        void shouldRejectBlankNameInCreate() {
            assertThatThrownBy(() -> AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "  ",
                    1,
                    NOW
            )).isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject non-positive number")
        void shouldRejectNonPositiveNumber() {
            assertThatThrownBy(() -> AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    0,
                    NOW
            )).isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("number");
        }

        @Test
        @DisplayName("should reject negative number")
        void shouldRejectNegativeNumber() {
            assertThatThrownBy(() -> AcademicLevel.create(
                    AcademicLevelId.generate(),
                    SCHOOL_ID,
                    "Primaria",
                    -1,
                    NOW
            )).isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("number");
        }
    }
}
