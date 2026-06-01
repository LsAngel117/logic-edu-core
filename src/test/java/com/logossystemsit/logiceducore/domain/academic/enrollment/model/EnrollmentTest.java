package com.logossystemsit.logiceducore.domain.academic.enrollment.model;

import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Enrollment domain")
class EnrollmentTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final EnrollmentId ENROLLMENT_ID = EnrollmentId.generate();
    private static final UserId USER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final GroupId GROUP_ID = GroupId.generate();

    // ======================== EnrollmentId ========================

    @Nested
    @DisplayName("EnrollmentId")
    class EnrollmentIdTests {

        @Test
        @DisplayName("should create valid EnrollmentId")
        void shouldCreateValidEnrollmentId() {
            EnrollmentId id = new EnrollmentId("123e4567-e89b-12d3-a456-426614174000");
            assertThat(id.value()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("should reject null or blank EnrollmentId")
        void shouldRejectNullOrBlankEnrollmentId(String value) {
            assertThatThrownBy(() -> new EnrollmentId(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("EnrollmentId");
        }

        @Test
        @DisplayName("should generate non-empty EnrollmentId")
        void shouldGenerateNonEmptyEnrollmentId() {
            assertThat(EnrollmentId.generate().value()).isNotBlank();
        }

        @Test
        @DisplayName("should generate unique EnrollmentIds")
        void shouldGenerateUniqueEnrollmentIds() {
            assertThat(EnrollmentId.generate())
                    .isNotEqualTo(EnrollmentId.generate());
        }
    }

    // ======================== EnrollmentStatus ========================

    @Nested
    @DisplayName("EnrollmentStatus")
    class EnrollmentStatusTests {

        @Test
        @DisplayName("should have three values")
        void shouldHaveThreeValues() {
            assertThat(EnrollmentStatus.values()).hasSize(3);
        }

        @ParameterizedTest
        @EnumSource(EnrollmentStatus.class)
        @DisplayName("should contain expected statuses")
        void shouldContainExpectedStatuses(EnrollmentStatus status) {
            assertThat(status).isIn(EnrollmentStatus.ACTIVE, EnrollmentStatus.INACTIVE, EnrollmentStatus.DROPPED);
        }
    }

    // ======================== Enrollment.create ========================

    @Nested
    @DisplayName("create factory")
    class CreateFactory {

        @Test
        @DisplayName("should create enrollment with ACTIVE status")
        void shouldCreateEnrollmentWithActiveStatus() {
            Enrollment enrollment = Enrollment.create(
                    ENROLLMENT_ID, USER_ID, GROUP_ID, FIXED_NOW);

            assertThat(enrollment.getId()).isEqualTo(ENROLLMENT_ID);
            assertThat(enrollment.getUserId()).isEqualTo(USER_ID);
            assertThat(enrollment.getGroupId()).isEqualTo(GROUP_ID);
            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
            assertThat(enrollment.getEnrolledAt()).isEqualTo(FIXED_NOW);
            assertThat(enrollment.getUpdatedAt()).isEqualTo(FIXED_NOW);
        }

        @Test
        @DisplayName("should reject null id")
        void shouldRejectNullId() {
            assertThatThrownBy(() -> Enrollment.create(
                    null, USER_ID, GROUP_ID, FIXED_NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("id");
        }

        @Test
        @DisplayName("should reject null userId")
        void shouldRejectNullUserId() {
            assertThatThrownBy(() -> Enrollment.create(
                    ENROLLMENT_ID, null, GROUP_ID, FIXED_NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("userId");
        }

        @Test
        @DisplayName("should reject null groupId")
        void shouldRejectNullGroupId() {
            assertThatThrownBy(() -> Enrollment.create(
                    ENROLLMENT_ID, USER_ID, null, FIXED_NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("groupId");
        }

        @Test
        @DisplayName("should reject null enrolledAt")
        void shouldRejectNullEnrolledAt() {
            assertThatThrownBy(() -> Enrollment.create(
                    ENROLLMENT_ID, USER_ID, GROUP_ID, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("enrolledAt");
        }
    }

    // ======================== Enrollment.restore ========================

    @Nested
    @DisplayName("restore factory")
    class RestoreFactory {

        @Test
        @DisplayName("should restore enrollment with given status")
        void shouldRestoreEnrollmentWithGivenStatus() {
            Enrollment enrollment = Enrollment.restore(
                    ENROLLMENT_ID, USER_ID, GROUP_ID,
                    EnrollmentStatus.INACTIVE, FIXED_NOW, LATER);

            assertThat(enrollment.getId()).isEqualTo(ENROLLMENT_ID);
            assertThat(enrollment.getUserId()).isEqualTo(USER_ID);
            assertThat(enrollment.getGroupId()).isEqualTo(GROUP_ID);
            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.INACTIVE);
            assertThat(enrollment.getEnrolledAt()).isEqualTo(FIXED_NOW);
            assertThat(enrollment.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("should restore enrollment with DROPPED status")
        void shouldRestoreEnrollmentWithDroppedStatus() {
            Enrollment enrollment = Enrollment.restore(
                    ENROLLMENT_ID, USER_ID, GROUP_ID,
                    EnrollmentStatus.DROPPED, FIXED_NOW, LATER);

            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
            assertThat(enrollment.getEnrolledAt()).isEqualTo(FIXED_NOW);
            assertThat(enrollment.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("should reject null status in restore")
        void shouldRejectNullStatusInRestore() {
            assertThatThrownBy(() -> Enrollment.restore(
                    ENROLLMENT_ID, USER_ID, GROUP_ID,
                    null, FIXED_NOW, LATER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("status");
        }
    }

    // ======================== Enrollment.drop ========================

    @Nested
    @DisplayName("drop behavior")
    class DropBehavior {

        @Test
        @DisplayName("should change status to DROPPED")
        void shouldChangeStatusToDropped() {
            Enrollment enrollment = Enrollment.create(
                    ENROLLMENT_ID, USER_ID, GROUP_ID, FIXED_NOW);
            Enrollment dropped = enrollment.drop(LATER);

            assertThat(dropped.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
            assertThat(dropped.getUpdatedAt()).isEqualTo(LATER);
            assertThat(dropped.getId()).isEqualTo(ENROLLMENT_ID);
            assertThat(dropped.getUserId()).isEqualTo(USER_ID);
            assertThat(dropped.getGroupId()).isEqualTo(GROUP_ID);
        }

        @Test
        @DisplayName("should be idempotent when already DROPPED")
        void shouldBeIdempotentWhenAlreadyDropped() {
            Enrollment enrollment = Enrollment.create(
                    ENROLLMENT_ID, USER_ID, GROUP_ID, FIXED_NOW);
            Enrollment dropped = enrollment.drop(LATER);
            Enrollment droppedAgain = dropped.drop(LATER.plusSeconds(3600));

            assertThat(droppedAgain.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
        }

        @Test
        @DisplayName("should preserve other fields on drop")
        void shouldPreserveOtherFieldsOnDrop() {
            Enrollment enrollment = Enrollment.restore(
                    ENROLLMENT_ID, USER_ID, GROUP_ID,
                    EnrollmentStatus.ACTIVE, FIXED_NOW, FIXED_NOW);
            Enrollment dropped = enrollment.drop(LATER);

            assertThat(dropped.getId()).isEqualTo(ENROLLMENT_ID);
            assertThat(dropped.getUserId()).isEqualTo(USER_ID);
            assertThat(dropped.getGroupId()).isEqualTo(GROUP_ID);
            assertThat(dropped.getEnrolledAt()).isEqualTo(FIXED_NOW);
        }
    }

    // ======================== equals/hashCode ========================

    @Nested
    @DisplayName("equals and hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("should be equal when same id")
        void shouldBeEqualWhenSameId() {
            Enrollment e1 = Enrollment.create(ENROLLMENT_ID, USER_ID, GROUP_ID, FIXED_NOW);
            Enrollment e2 = Enrollment.restore(ENROLLMENT_ID, USER_ID, GROUP_ID,
                    EnrollmentStatus.DROPPED, FIXED_NOW, LATER);

            assertThat(e1).isEqualTo(e2);
            assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when different ids")
        void shouldNotBeEqualWhenDifferentIds() {
            Enrollment e1 = Enrollment.create(ENROLLMENT_ID, USER_ID, GROUP_ID, FIXED_NOW);
            Enrollment e2 = Enrollment.create(EnrollmentId.generate(),
                    USER_ID, GROUP_ID, FIXED_NOW);

            assertThat(e1).isNotEqualTo(e2);
        }
    }
}
