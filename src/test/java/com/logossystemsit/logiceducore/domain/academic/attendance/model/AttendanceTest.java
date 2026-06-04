package com.logossystemsit.logiceducore.domain.academic.attendance.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject.AttendanceId;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject.AttendanceStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Attendance domain")
class AttendanceTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final AttendanceId ATTENDANCE_ID = AttendanceId.generate();
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final UserId STUDENT_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final LocalDate ATTENDANCE_DATE = LocalDate.of(2026, 6, 1);

    // ======================== AttendanceId ========================

    @Nested
    @DisplayName("AttendanceId")
    class AttendanceIdTests {

        @Test
        @DisplayName("should create valid AttendanceId")
        void shouldCreateValidAttendanceId() {
            AttendanceId id = new AttendanceId("123e4567-e89b-12d3-a456-426614174000");
            assertThat(id.value()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("should reject null or blank AttendanceId")
        void shouldRejectNullOrBlankAttendanceId(String value) {
            assertThatThrownBy(() -> new AttendanceId(value))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("AttendanceId");
        }

        @Test
        @DisplayName("should generate non-empty AttendanceId")
        void shouldGenerateNonEmptyAttendanceId() {
            assertThat(AttendanceId.generate().value()).isNotBlank();
        }

        @Test
        @DisplayName("should generate unique AttendanceIds")
        void shouldGenerateUniqueAttendanceIds() {
            assertThat(AttendanceId.generate().value())
                    .isNotEqualTo(AttendanceId.generate().value());
        }
    }

    // ======================== AttendanceStatus ========================

    @Nested
    @DisplayName("AttendanceStatus")
    class AttendanceStatusTests {

        @Test
        @DisplayName("should have four expected values")
        void shouldHaveFourExpectedValues() {
            assertThat(AttendanceStatus.values()).containsExactly(
                    AttendanceStatus.PRESENT,
                    AttendanceStatus.ABSENT,
                    AttendanceStatus.LATE,
                    AttendanceStatus.EXCUSED
            );
        }

        @ParameterizedTest
        @EnumSource(AttendanceStatus.class)
        @DisplayName("should resolve all enum values")
        void shouldResolveAllEnumValues(AttendanceStatus status) {
            assertThat(AttendanceStatus.valueOf(status.name())).isEqualTo(status);
        }
    }

    // ======================== Attendance.create ========================

    @Nested
    @DisplayName("Attendance.create")
    class AttendanceCreateTests {

        @Test
        @DisplayName("should create attendance with valid data")
        void shouldCreateWithValidData() {
            Attendance attendance = Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.PRESENT, null, FIXED_NOW
            );

            assertThat(attendance.getId()).isEqualTo(ATTENDANCE_ID);
            assertThat(attendance.getGroupId()).isEqualTo(GROUP_ID);
            assertThat(attendance.getStudentId()).isEqualTo(STUDENT_ID);
            assertThat(attendance.getDate()).isEqualTo(ATTENDANCE_DATE);
            assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
            assertThat(attendance.getObservations()).isNull();
            assertThat(attendance.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(attendance.getUpdatedAt()).isEqualTo(FIXED_NOW);
        }

        @Test
        @DisplayName("should create attendance with observations")
        void shouldCreateWithObservations() {
            Attendance attendance = Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.LATE, "Arrived 15 minutes late", FIXED_NOW
            );

            assertThat(attendance.getObservations()).isEqualTo("Arrived 15 minutes late");
            assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LATE);
        }

        @Test
        @DisplayName("should create attendance for EXCUSED status")
        void shouldCreateExcusedAttendance() {
            Attendance attendance = Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.EXCUSED, "Medical excuse provided", FIXED_NOW
            );

            assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.EXCUSED);
        }

        @Test
        @DisplayName("should reject null AttendanceId")
        void shouldRejectNullId() {
            assertThatThrownBy(() -> Attendance.create(
                    null, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.PRESENT, null, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("id");
        }

        @Test
        @DisplayName("should reject null GroupId")
        void shouldRejectNullGroupId() {
            assertThatThrownBy(() -> Attendance.create(
                    ATTENDANCE_ID, null, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.PRESENT, null, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("groupId");
        }

        @Test
        @DisplayName("should reject null StudentId")
        void shouldRejectNullStudentId() {
            assertThatThrownBy(() -> Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, null, ATTENDANCE_DATE,
                    AttendanceStatus.PRESENT, null, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("studentId");
        }

        @Test
        @DisplayName("should reject null date")
        void shouldRejectNullDate() {
            assertThatThrownBy(() -> Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, null,
                    AttendanceStatus.PRESENT, null, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("date");
        }

        @Test
        @DisplayName("should reject null status")
        void shouldRejectNullStatus() {
            assertThatThrownBy(() -> Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    null, null, FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("status");
        }
    }

    // ======================== Attendance.restore ========================

    @Nested
    @DisplayName("Attendance.restore")
    class AttendanceRestoreTests {

        @Test
        @DisplayName("should restore attendance from persistence")
        void shouldRestoreFromPersistence() {
            Attendance attendance = Attendance.restore(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.ABSENT, "Unexcused",
                    FIXED_NOW, LATER
            );

            assertThat(attendance.getId()).isEqualTo(ATTENDANCE_ID);
            assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
            assertThat(attendance.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(attendance.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("should restore with null observations")
        void shouldRestoreWithNullObservations() {
            Attendance attendance = Attendance.restore(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.PRESENT, null,
                    FIXED_NOW, FIXED_NOW
            );

            assertThat(attendance.getObservations()).isNull();
        }
    }

    // ======================== Attendance.changeStatus ========================

    @Nested
    @DisplayName("Attendance.changeStatus")
    class AttendanceChangeStatusTests {

        @Test
        @DisplayName("should change status from PRESENT to ABSENT")
        void shouldChangeStatusFromPresentToAbsent() {
            Attendance attendance = createSampleAttendance();
            Attendance changed = attendance.changeStatus(AttendanceStatus.ABSENT, "Left early", LATER);

            assertThat(changed.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
            assertThat(changed.getObservations()).isEqualTo("Left early");
            assertThat(changed.getUpdatedAt()).isEqualTo(LATER);
            assertThat(changed.getId()).isEqualTo(attendance.getId());
            assertThat(changed.getCreatedAt()).isEqualTo(attendance.getCreatedAt());
        }

        @Test
        @DisplayName("should change status to same status")
        void shouldChangeStatusToSameStatus() {
            Attendance attendance = createSampleAttendance();
            Attendance changed = attendance.changeStatus(AttendanceStatus.PRESENT, null, LATER);

            assertThat(changed.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
            assertThat(changed.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("should clear observations when changing to null")
        void shouldClearObservationsWhenChangingToNull() {
            Attendance attendance = Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.LATE, "Arrived late", FIXED_NOW
            );
            Attendance changed = attendance.changeStatus(AttendanceStatus.PRESENT, null, LATER);

            assertThat(changed.getObservations()).isNull();
            assertThat(changed.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        }

        @Test
        @DisplayName("should reject null status on change")
        void shouldRejectNullStatusOnChange() {
            Attendance attendance = createSampleAttendance();
            assertThatThrownBy(() -> attendance.changeStatus(null, null, LATER))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("status");
        }
    }

    // ======================== Attendance equality ========================

    @Nested
    @DisplayName("Attendance equality")
    class AttendanceEqualityTests {

        @Test
        @DisplayName("should equal when same ID")
        void shouldEqualWhenSameId() {
            Attendance a1 = createSampleAttendance();
            Attendance a2 = Attendance.restore(
                    a1.getId(), a1.getGroupId(), a1.getStudentId(), a1.getDate(),
                    a1.getStatus(), a1.getObservations(),
                    a1.getCreatedAt(), a1.getUpdatedAt()
            );
            assertThat(a1).isEqualTo(a2);
        }

        @Test
        @DisplayName("should not equal when different ID")
        void shouldNotEqualWhenDifferentId() {
            Attendance a1 = createSampleAttendance();
            Attendance a2 = Attendance.create(
                    AttendanceId.generate(), a1.getGroupId(), a1.getStudentId(),
                    a1.getDate(), a1.getStatus(), null, FIXED_NOW
            );
            assertThat(a1).isNotEqualTo(a2);
        }
    }

    // ======================== helpers ========================

    private Attendance createSampleAttendance() {
        return Attendance.create(
                ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                AttendanceStatus.PRESENT, null, FIXED_NOW
        );
    }
}
