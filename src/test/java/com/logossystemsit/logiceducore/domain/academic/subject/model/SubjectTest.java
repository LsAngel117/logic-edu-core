package com.logossystemsit.logiceducore.domain.academic.subject.model;

import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Subject domain aggregate")
class SubjectTest {

    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @Nested
    @DisplayName("SubjectId value object")
    class SubjectIdTest {

        @Test
        @DisplayName("should generate a non-blank UUID")
        void shouldGenerateNonBlankUuid() {
            SubjectId id = SubjectId.generate();
            assertThat(id.value()).isNotBlank();
            assertThat(id.value()).containsPattern(
                    "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValue() {
            assertThatThrownBy(() -> new SubjectId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
        }

        @Test
        @DisplayName("should reject blank value")
        void shouldRejectBlankValue() {
            assertThatThrownBy(() -> new SubjectId("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("required");
        }

        @Test
        @DisplayName("should trim whitespace from value")
        void shouldTrimWhitespaceFromValue() {
            SubjectId id = new SubjectId("  abc-123  ");
            assertThat(id.value()).isEqualTo("abc-123");
        }
    }

    @Nested
    @DisplayName("SubjectStatus enum")
    class SubjectStatusTest {

        @Test
        @DisplayName("should contain ACTIVE and INACTIVE")
        void shouldContainActiveAndInactive() {
            assertThat(SubjectStatus.values()).containsExactly(
                    SubjectStatus.ACTIVE,
                    SubjectStatus.INACTIVE
            );
        }
    }

    @Nested
    @DisplayName("create factory")
    class CreateFactory {

        @Test
        @DisplayName("should create an active subject with all data")
        void shouldCreateActiveSubjectWithAllData() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    "Basic math course",
                    120,
                    NOW
            );

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ACTIVE);
            assertThat(subject.getSchoolId()).isEqualTo(SCHOOL_ID);
            assertThat(subject.getCode()).isEqualTo("MAT101");
            assertThat(subject.getName()).isEqualTo("Mathematics");
            assertThat(subject.getDescription()).isEqualTo("Basic math course");
            assertThat(subject.getHours()).isEqualTo(120);
            assertThat(subject.getCreatedAt()).isEqualTo(NOW);
            assertThat(subject.getUpdatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("should create subject with null description")
        void shouldCreateSubjectWithNullDescription() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "PHY201",
                    "Physics",
                    null,
                    80,
                    NOW
            );

            assertThat(subject.getDescription()).isNull();
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ACTIVE);
            assertThat(subject.getName()).isEqualTo("Physics");
        }

        @Test
        @DisplayName("should create subject with zero hours")
        void shouldCreateSubjectWithZeroHours() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "LAB01",
                    "Laboratory",
                    "Lab session",
                    0,
                    NOW
            );

            assertThat(subject.getHours()).isEqualTo(0);
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ACTIVE);
        }

        @Test
        @DisplayName("should create subject with different data")
        void shouldCreateSubjectWithDifferentData() {
            SchoolId otherSchool = new SchoolId("880e8400-e29b-41d4-a716-446655440003");

            Subject subject = Subject.create(
                    SubjectId.generate(),
                    otherSchool,
                    "HIS301",
                    "History",
                    "World history",
                    90,
                    NOW
            );

            assertThat(subject.getCode()).isEqualTo("HIS301");
            assertThat(subject.getName()).isEqualTo("History");
            assertThat(subject.getHours()).isEqualTo(90);
            assertThat(subject.getSchoolId()).isEqualTo(otherSchool);
        }
    }

    @Nested
    @DisplayName("restore factory")
    class RestoreFactory {

        @Test
        @DisplayName("should restore with explicit state")
        void shouldRestoreWithExplicitState() {
            Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
            SubjectId id = new SubjectId("abc-123");

            Subject subject = Subject.restore(
                    id,
                    SCHOOL_ID,
                    "BIO101",
                    "Biology",
                    "Intro to biology",
                    100,
                    SubjectStatus.INACTIVE,
                    createdAt,
                    NOW
            );

            assertThat(subject.getId()).isEqualTo(id);
            assertThat(subject.getSchoolId()).isEqualTo(SCHOOL_ID);
            assertThat(subject.getCode()).isEqualTo("BIO101");
            assertThat(subject.getName()).isEqualTo("Biology");
            assertThat(subject.getDescription()).isEqualTo("Intro to biology");
            assertThat(subject.getHours()).isEqualTo(100);
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.INACTIVE);
            assertThat(subject.getCreatedAt()).isEqualTo(createdAt);
            assertThat(subject.getUpdatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("changeData behavior")
    class ChangeDataBehavior {

        @Test
        @DisplayName("should return new instance with updated fields")
        void shouldReturnNewInstanceWithUpdatedFields() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    "Basic math",
                    120,
                    NOW
            );

            Subject updated = subject.changeData(
                    "MAT102", "Advanced Math", "Advanced course", 150,
                    NOW.plusSeconds(3600)
            );

            assertThat(updated).isNotSameAs(subject);
            assertThat(updated.getCode()).isEqualTo("MAT102");
            assertThat(updated.getName()).isEqualTo("Advanced Math");
            assertThat(updated.getDescription()).isEqualTo("Advanced course");
            assertThat(updated.getHours()).isEqualTo(150);
            assertThat(updated.getId()).isEqualTo(subject.getId());
            assertThat(updated.getSchoolId()).isEqualTo(subject.getSchoolId());
            assertThat(updated.getStatus()).isEqualTo(subject.getStatus());
            assertThat(updated.getCreatedAt()).isEqualTo(subject.getCreatedAt());
            assertThat(updated.getUpdatedAt()).isEqualTo(NOW.plusSeconds(3600));
        }

        @Test
        @DisplayName("should reject null code")
        void shouldRejectNullCode() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            );

            assertThatThrownBy(() -> subject.changeData(null, "Name", null, 120, NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("code");
        }

        @Test
        @DisplayName("should reject blank code")
        void shouldRejectBlankCode() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            );

            assertThatThrownBy(() -> subject.changeData("  ", "Name", null, 120, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("code");
        }

        @Test
        @DisplayName("should reject null name")
        void shouldRejectNullName() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            );

            assertThatThrownBy(() -> subject.changeData("MAT102", null, null, 120, NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject blank name")
        void shouldRejectBlankName() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            );

            assertThatThrownBy(() -> subject.changeData("MAT102", "  ", null, 120, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject negative hours")
        void shouldRejectNegativeHours() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            );

            assertThatThrownBy(() -> subject.changeData("MAT102", "Name", null, -1, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("hours");
        }
    }

    @Nested
    @DisplayName("deactivate behavior")
    class DeactivateBehavior {

        @Test
        @DisplayName("should set status to INACTIVE")
        void shouldSetStatusToInactive() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            );

            Subject deactivated = subject.deactivate(NOW.plusSeconds(3600));

            assertThat(deactivated.getStatus()).isEqualTo(SubjectStatus.INACTIVE);
            assertThat(deactivated.getId()).isEqualTo(subject.getId());
            assertThat(deactivated.getName()).isEqualTo(subject.getName());
            assertThat(deactivated.getCode()).isEqualTo(subject.getCode());
            assertThat(deactivated.getHours()).isEqualTo(subject.getHours());
        }

        @Test
        @DisplayName("should return same state if already inactive (idempotent)")
        void shouldReturnSameStateIfAlreadyInactive() {
            Subject inactive = Subject.restore(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    SubjectStatus.INACTIVE,
                    NOW,
                    NOW
            );

            Subject result = inactive.deactivate(NOW.plusSeconds(3600));

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.INACTIVE);
            assertThat(result).isEqualTo(inactive);
        }
    }

    @Nested
    @DisplayName("immutability")
    class Immutability {

        @Test
        @DisplayName("should have no public setters")
        void shouldHaveNoPublicSetters() {
            var methods = Subject.class.getMethods();
            for (var method : methods) {
                assertThat(method.getName()).doesNotStartWith("set");
            }
        }

        @Test
        @DisplayName("deactivate produces a different instance")
        void deactivateProducesDifferentInstance() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            );

            Subject deactivated = subject.deactivate(NOW.plusSeconds(3600));

            assertThat(deactivated).isNotSameAs(subject);
        }

        @Test
        @DisplayName("changeData produces a different instance")
        void changeDataProducesDifferentInstance() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            );

            Subject updated = subject.changeData("MAT101", "Math", null, 120, NOW.plusSeconds(3600));

            assertThat(updated).isNotSameAs(subject);
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should reject null SubjectId in create")
        void shouldRejectNullSubjectIdInCreate() {
            assertThatThrownBy(() -> Subject.create(
                    null,
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            )).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should reject null SchoolId in create")
        void shouldRejectNullSchoolIdInCreate() {
            assertThatThrownBy(() -> Subject.create(
                    SubjectId.generate(),
                    null,
                    "MAT101",
                    "Mathematics",
                    null,
                    120,
                    NOW
            )).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should reject null code in create")
        void shouldRejectNullCodeInCreate() {
            assertThatThrownBy(() -> Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    null,
                    "Mathematics",
                    null,
                    120,
                    NOW
            )).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("code");
        }

        @Test
        @DisplayName("should reject blank code in create")
        void shouldRejectBlankCodeInCreate() {
            assertThatThrownBy(() -> Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "  ",
                    "Mathematics",
                    null,
                    120,
                    NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("code");
        }

        @Test
        @DisplayName("should reject null name in create")
        void shouldRejectNullNameInCreate() {
            assertThatThrownBy(() -> Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    null,
                    null,
                    120,
                    NOW
            )).isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject blank name in create")
        void shouldRejectBlankNameInCreate() {
            assertThatThrownBy(() -> Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "  ",
                    null,
                    120,
                    NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("should reject negative hours in create")
        void shouldRejectNegativeHoursInCreate() {
            assertThatThrownBy(() -> Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    -1,
                    NOW
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("hours");
        }

        @Test
        @DisplayName("should allow zero hours in create")
        void shouldAllowZeroHoursInCreate() {
            Subject subject = Subject.create(
                    SubjectId.generate(),
                    SCHOOL_ID,
                    "MAT101",
                    "Mathematics",
                    null,
                    0,
                    NOW
            );

            assertThat(subject.getHours()).isEqualTo(0);
        }
    }
}
