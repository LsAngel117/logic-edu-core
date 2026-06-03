package com.logossystemsit.logiceducore.domain.academic.structure.model;

import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.StructureType;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AcademicStructure domain aggregate")
class AcademicStructureTest {

    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @Nested
    @DisplayName("AcademicStructureId value object")
    class AcademicStructureIdTest {

        @Test
        @DisplayName("should generate a non-blank UUID")
        void shouldGenerateNonBlankUuid() {
            AcademicStructureId id = AcademicStructureId.generate();
            assertThat(id.value()).isNotBlank();
            assertThat(id.value()).containsPattern(
                    "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValue() {
            assertThatThrownBy(() -> new AcademicStructureId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
        }

        @Test
        @DisplayName("should reject blank value")
        void shouldRejectBlankValue() {
            assertThatThrownBy(() -> new AcademicStructureId("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
        }
    }

    @Nested
    @DisplayName("StructureType enum")
    class StructureTypeTest {

        @Test
        @DisplayName("should contain all expected values")
        void shouldContainAllExpectedValues() {
            assertThat(StructureType.values()).containsExactly(
                    StructureType.SEMESTRAL,
                    StructureType.TRIMESTRAL,
                    StructureType.ANUAL,
                    StructureType.CICLO,
                    StructureType.MODULAR
            );
        }
    }

    @Nested
    @DisplayName("create factory")
    class CreateFactory {

        @Test
        @DisplayName("should create an active structure with version 1")
        void shouldCreateActiveStructureWithVersionOne() {
            AcademicStructure structure = AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.SEMESTRAL,
                    10,
                    2,
                    3,
                    6,
                    45,
                    NOW
            );

            assertThat(structure.isActive()).isTrue();
            assertThat(structure.getVersion()).isEqualTo(1);
            assertThat(structure.getStructureType()).isEqualTo(StructureType.SEMESTRAL);
            assertThat(structure.getLevelsCount()).isEqualTo(10);
            assertThat(structure.getPeriodsPerLevel()).isEqualTo(2);
            assertThat(structure.getEvaluationPeriodsPerPeriod()).isEqualTo(3);
            assertThat(structure.getSubjectsPerPeriod()).isEqualTo(6);
            assertThat(structure.getHoursPerSubject()).isEqualTo(45);
            assertThat(structure.getCreatedAt()).isEqualTo(NOW);
            assertThat(structure.getUpdatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("should allow zero evaluation periods")
        void shouldAllowZeroEvaluationPeriods() {
            AcademicStructure structure = AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.ANUAL,
                    1,
                    3,
                    0,
                    5,
                    40,
                    NOW
            );

            assertThat(structure.getEvaluationPeriodsPerPeriod()).isZero();
            assertThat(structure.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("restore factory")
    class RestoreFactory {

        @Test
        @DisplayName("should restore with explicit state")
        void shouldRestoreWithExplicitState() {
            Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
            AcademicStructureId id = new AcademicStructureId("abc-123");

            AcademicStructure structure = AcademicStructure.restore(
                    id,
                    SCHOOL_ID,
                    StructureType.MODULAR,
                    4,
                    3,
                    2,
                    8,
                    60,
                    3,
                    false,
                    createdAt,
                    NOW
            );

            assertThat(structure.getId()).isEqualTo(id);
            assertThat(structure.getVersion()).isEqualTo(3);
            assertThat(structure.isActive()).isFalse();
            assertThat(structure.getCreatedAt()).isEqualTo(createdAt);
            assertThat(structure.getUpdatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("deactivate behavior")
    class DeactivateBehavior {

        @Test
        @DisplayName("should set active to false")
        void shouldSetActiveToFalse() {
            AcademicStructure structure = AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.TRIMESTRAL,
                    12,
                    3,
                    2,
                    7,
                    50,
                    NOW
            );

            AcademicStructure deactivated = structure.deactivate(NOW.plusSeconds(3600));

            assertThat(deactivated.isActive()).isFalse();
            assertThat(deactivated.getId()).isEqualTo(structure.getId());
            assertThat(deactivated.getVersion()).isEqualTo(structure.getVersion());
        }

        @Test
        @DisplayName("should return same state if already inactive")
        void shouldReturnSameStateIfAlreadyInactive() {
            AcademicStructure inactive = AcademicStructure.restore(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.CICLO,
                    2,
                    2,
                    1,
                    4,
                    40,
                    1,
                    false,
                    NOW,
                    NOW
            );

            AcademicStructure result = inactive.deactivate(NOW.plusSeconds(3600));

            assertThat(result.isActive()).isFalse();
            assertThat(result).isEqualTo(inactive);
        }
    }

    @Nested
    @DisplayName("changeVersion behavior")
    class ChangeVersionBehavior {

        @Test
        @DisplayName("should create new structure with incremented version")
        void shouldCreateNewStructureWithIncrementedVersion() {
            AcademicStructure structure = AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.SEMESTRAL,
                    10,
                    2,
                    3,
                    6,
                    45,
                    NOW
            );

            Instant later = NOW.plusSeconds(86400);
            AcademicStructure newVersion = structure.changeVersion(
                    StructureType.ANUAL,
                    12,
                    3,
                    2,
                    8,
                    50,
                    later
            );

            assertThat(newVersion.getId()).isNotEqualTo(structure.getId());
            assertThat(newVersion.getVersion()).isEqualTo(2);
            assertThat(newVersion.getStructureType()).isEqualTo(StructureType.ANUAL);
            assertThat(newVersion.getLevelsCount()).isEqualTo(12);
            assertThat(newVersion.getPeriodsPerLevel()).isEqualTo(3);
            assertThat(newVersion.getEvaluationPeriodsPerPeriod()).isEqualTo(2);
            assertThat(newVersion.getSubjectsPerPeriod()).isEqualTo(8);
            assertThat(newVersion.getHoursPerSubject()).isEqualTo(50);
            assertThat(newVersion.isActive()).isTrue();
            assertThat(newVersion.getSchoolId()).isEqualTo(structure.getSchoolId());
            assertThat(newVersion.getCreatedAt()).isEqualTo(later);
        }

        @Test
        @DisplayName("should keep incrementing version across multiple changes")
        void shouldKeepIncrementingVersionAcrossMultipleChanges() {
            AcademicStructure v1 = AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.SEMESTRAL,
                    10,
                    2,
                    0,
                    5,
                    40,
                    NOW
            );

            AcademicStructure v2 = v1.changeVersion(
                    StructureType.TRIMESTRAL,
                    12,
                    3,
                    2,
                    6,
                    45,
                    NOW.plusSeconds(86400)
            );

            AcademicStructure v3 = v2.changeVersion(
                    StructureType.ANUAL,
                    3,
                    6,
                    4,
                    10,
                    50,
                    NOW.plusSeconds(172800)
            );

            assertThat(v2.getVersion()).isEqualTo(2);
            assertThat(v3.getVersion()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("immutability")
    class Immutability {

        @Test
        @DisplayName("should have no public setters")
        void shouldHaveNoPublicSetters() {
            var methods = AcademicStructure.class.getMethods();
            for (var method : methods) {
                assertThat(method.getName()).doesNotStartWith("set");
            }
        }

        @Test
        @DisplayName("deactivate produces a different instance")
        void deactivateProducesDifferentInstance() {
            AcademicStructure structure = AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.SEMESTRAL,
                    1, 2, 0, 5, 40, NOW
            );

            AcademicStructure deactivated = structure.deactivate(NOW.plusSeconds(3600));

            assertThat(deactivated).isNotSameAs(structure);
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should reject negative levels count")
        void shouldRejectNegativeLevelsCount() {
            assertThatThrownBy(() -> AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.SEMESTRAL,
                    -1, 2, 0, 5, 40, NOW
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should reject negative periods per level")
        void shouldRejectNegativePeriodsPerLevel() {
            assertThatThrownBy(() -> AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    StructureType.SEMESTRAL,
                    1, -1, 0, 5, 40, NOW
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should reject null SchoolId")
        void shouldRejectNullSchoolId() {
            assertThatThrownBy(() -> AcademicStructure.create(
                    AcademicStructureId.generate(),
                    null,
                    StructureType.SEMESTRAL,
                    1, 2, 0, 5, 40, NOW
            )).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should reject null StructureType")
        void shouldRejectNullStructureType() {
            assertThatThrownBy(() -> AcademicStructure.create(
                    AcademicStructureId.generate(),
                    SCHOOL_ID,
                    null,
                    1, 2, 0, 5, 40, NOW
            )).isInstanceOf(NullPointerException.class);
        }
    }
}
