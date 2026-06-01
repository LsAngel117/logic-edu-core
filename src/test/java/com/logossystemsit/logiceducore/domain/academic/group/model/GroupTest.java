package com.logossystemsit.logiceducore.domain.academic.group.model;

import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Group domain")
class GroupTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId SUBJECT_ID =
            new com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId("660e8400-e29b-41d4-a716-446655440001");
    private static final AcademicPeriodId PERIOD_ID =
            new AcademicPeriodId("770e8400-e29b-41d4-a716-446655440002");
    private static final BranchId BRANCH_ID =
            BranchId.of("880e8400-e29b-41d4-a716-446655440003");
    private static final UserId TEACHER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");

    private static Schedule createValidSchedule() {
        return new Schedule(
                ScheduleId.generate(),
                "MONDAY",
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                "Room 101"
        );
    }

    private static Schedule createSchedule(String day, int startHour, int endHour) {
        return new Schedule(
                ScheduleId.generate(),
                day,
                LocalTime.of(startHour, 0),
                LocalTime.of(endHour, 0),
                null
        );
    }

    // ======================== GroupId ========================

    @Nested
    @DisplayName("GroupId")
    class GroupIdTests {

        @Test
        @DisplayName("should create valid GroupId")
        void shouldCreateValidGroupId() {
            GroupId id = new GroupId("123e4567-e89b-12d3-a456-426614174000");
            assertThat(id.value()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("should reject null or blank GroupId")
        void shouldRejectNullOrBlankGroupId(String value) {
            assertThatThrownBy(() -> new GroupId(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("GroupId");
        }

        @Test
        @DisplayName("should generate non-empty GroupId")
        void shouldGenerateNonEmptyGroupId() {
            GroupId id = GroupId.generate();
            assertThat(id.value()).isNotNull();
            assertThat(id.value()).isNotBlank();
        }

        @Test
        @DisplayName("should generate unique GroupIds")
        void shouldGenerateUniqueGroupIds() {
            GroupId id1 = GroupId.generate();
            GroupId id2 = GroupId.generate();
            assertThat(id1.value()).isNotEqualTo(id2.value());
        }
    }

    // ======================== GroupStatus ========================

    @Nested
    @DisplayName("GroupStatus")
    class GroupStatusTests {

        @Test
        @DisplayName("should have ACTIVE and INACTIVE values")
        void shouldHaveActiveAndInactiveValues() {
            assertThat(GroupStatus.values()).containsExactly(GroupStatus.ACTIVE, GroupStatus.INACTIVE);
        }

        @Test
        @DisplayName("ACTIVE should not equal INACTIVE")
        void activeShouldNotEqualInactive() {
            assertThat(GroupStatus.ACTIVE).isNotEqualTo(GroupStatus.INACTIVE);
        }
    }

    // ======================== Schedule ========================

    @Nested
    @DisplayName("Schedule")
    class ScheduleTests {

        @Test
        @DisplayName("should create valid schedule")
        void shouldCreateValidSchedule() {
            Schedule schedule = new Schedule(
                    ScheduleId.generate(),
                    "MONDAY",
                    LocalTime.of(8, 0),
                    LocalTime.of(10, 0),
                    "Room 101"
            );
            assertThat(schedule.scheduleId()).isNotNull();
            assertThat(schedule.dayOfWeek()).isEqualTo("MONDAY");
            assertThat(schedule.startTime()).isEqualTo(LocalTime.of(8, 0));
            assertThat(schedule.endTime()).isEqualTo(LocalTime.of(10, 0));
            assertThat(schedule.classroom()).isEqualTo("Room 101");
        }

        @Test
        @DisplayName("should allow null classroom")
        void shouldAllowNullClassroom() {
            Schedule schedule = new Schedule(
                    ScheduleId.generate(),
                    "TUESDAY",
                    LocalTime.of(14, 0),
                    LocalTime.of(16, 0),
                    null
            );
            assertThat(schedule.classroom()).isNull();
            assertThat(schedule.dayOfWeek()).isEqualTo("TUESDAY");
        }

        @Test
        @DisplayName("should reject startTime after endTime")
        void shouldRejectStartTimeAfterEndTime() {
            assertThatThrownBy(() -> new Schedule(
                    ScheduleId.generate(),
                    "MONDAY",
                    LocalTime.of(10, 0),
                    LocalTime.of(8, 0),
                    null
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("startTime must be before endTime");
        }

        @Test
        @DisplayName("should reject startTime equal to endTime")
        void shouldRejectStartTimeEqualToEndTime() {
            assertThatThrownBy(() -> new Schedule(
                    ScheduleId.generate(),
                    "MONDAY",
                    LocalTime.of(10, 0),
                    LocalTime.of(10, 0),
                    null
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("startTime must be before endTime");
        }

        @Test
        @DisplayName("should accept all days of the week")
        void shouldAcceptAllDaysOfWeek() {
            String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
            for (String day : days) {
                Schedule schedule = new Schedule(
                        ScheduleId.generate(),
                        day,
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0),
                        null
                );
                assertThat(schedule.dayOfWeek()).isEqualTo(day);
            }
        }
    }

    // ======================== Group ========================

    @Nested
    @DisplayName("Group.create")
    class GroupCreateTests {

        @Test
        @DisplayName("should create group with valid data")
        void shouldCreateGroupWithValidData() {
            List<Schedule> schedules = List.of(createValidSchedule());
            Group group = Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, schedules, FIXED_NOW
            );

            assertThat(group.getId()).isEqualTo(GROUP_ID);
            assertThat(group.getSchoolId()).isEqualTo(SCHOOL_ID);
            assertThat(group.getSubjectId()).isEqualTo(SUBJECT_ID);
            assertThat(group.getAcademicPeriodId()).isEqualTo(PERIOD_ID);
            assertThat(group.getBranchId()).isEqualTo(BRANCH_ID);
            assertThat(group.getTeacherId()).isEqualTo(TEACHER_ID);
            assertThat(group.getCode()).isEqualTo("MATH-101");
            assertThat(group.getCapacity()).isEqualTo(30);
            assertThat(group.getStatus()).isEqualTo(GroupStatus.ACTIVE);
            assertThat(group.getSchedules()).hasSize(1);
            assertThat(group.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(group.getUpdatedAt()).isEqualTo(FIXED_NOW);
        }

        @Test
        @DisplayName("should create group with empty schedules")
        void shouldCreateGroupWithEmptySchedules() {
            Group group = Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, List.of(), FIXED_NOW
            );
            assertThat(group.getSchedules()).isEmpty();
        }

        @Test
        @DisplayName("should trim code")
        void shouldTrimCode() {
            Group group = Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "  MATH-101  ", 30, List.of(), FIXED_NOW
            );
            assertThat(group.getCode()).isEqualTo("MATH-101");
        }

        @Test
        @DisplayName("should reject null code")
        void shouldRejectNullCode() {
            assertThatThrownBy(() -> Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, null, 30, List.of(), FIXED_NOW
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("code");
        }

        @Test
        @DisplayName("should reject blank code")
        void shouldRejectBlankCode() {
            assertThatThrownBy(() -> Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "  ", 30, List.of(), FIXED_NOW
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("code");
        }

        @Test
        @DisplayName("should reject capacity <= 0")
        void shouldRejectZeroCapacity() {
            assertThatThrownBy(() -> Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 0, List.of(), FIXED_NOW
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("capacity");
        }

        @Test
        @DisplayName("should reject negative capacity")
        void shouldRejectNegativeCapacity() {
            assertThatThrownBy(() -> Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", -1, List.of(), FIXED_NOW
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("capacity");
        }

        @Test
        @DisplayName("should accept capacity of 1")
        void shouldAcceptCapacityOfOne() {
            Group group = Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 1, List.of(), FIXED_NOW
            );
            assertThat(group.getCapacity()).isEqualTo(1);
        }

        @Test
        @DisplayName("should accept large capacity")
        void shouldAcceptLargeCapacity() {
            Group group = Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 500, List.of(), FIXED_NOW
            );
            assertThat(group.getCapacity()).isEqualTo(500);
        }
    }

    @Nested
    @DisplayName("Group.restore")
    class GroupRestoreTests {

        @Test
        @DisplayName("should restore group from persistence")
        void shouldRestoreGroupFromPersistence() {
            List<Schedule> schedules = List.of(createValidSchedule());
            Group group = Group.restore(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, schedules,
                    GroupStatus.ACTIVE, 1L, FIXED_NOW, LATER
            );

            assertThat(group.getId()).isEqualTo(GROUP_ID);
            assertThat(group.getCode()).isEqualTo("MATH-101");
            assertThat(group.getStatus()).isEqualTo(GroupStatus.ACTIVE);
            assertThat(group.getVersion()).isEqualTo(1L);
            assertThat(group.getSchedules()).hasSize(1);
            assertThat(group.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(group.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("should restore INACTIVE group")
        void shouldRestoreInactiveGroup() {
            Group group = Group.restore(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, List.of(),
                    GroupStatus.INACTIVE, 0L, FIXED_NOW, FIXED_NOW
            );
            assertThat(group.getStatus()).isEqualTo(GroupStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("Group.changeData")
    class GroupChangeDataTests {

        @Test
        @DisplayName("should change all mutable fields except schedules")
        void shouldChangeAllMutableFields() {
            Group group = createActiveGroup();

            AcademicPeriodId newPeriodId = new AcademicPeriodId("aaa00001-e29b-41d4-a716-446655440001");
            BranchId newBranchId = BranchId.of("bbb00002-e29b-41d4-a716-446655440002");
            UserId newTeacherId = new UserId("ccc00001-e29b-41d4-a716-446655440003");

            Group updated = group.changeData(
                    newPeriodId, newBranchId, newTeacherId,
                    "MATH-202", 25, LATER
            );

            assertThat(updated.getAcademicPeriodId()).isEqualTo(newPeriodId);
            assertThat(updated.getBranchId()).isEqualTo(newBranchId);
            assertThat(updated.getTeacherId()).isEqualTo(newTeacherId);
            assertThat(updated.getCode()).isEqualTo("MATH-202");
            assertThat(updated.getCapacity()).isEqualTo(25);
            assertThat(updated.getUpdatedAt()).isEqualTo(LATER);
            // Immutable fields unchanged
            assertThat(updated.getId()).isEqualTo(group.getId());
            assertThat(updated.getSchoolId()).isEqualTo(group.getSchoolId());
            assertThat(updated.getSubjectId()).isEqualTo(group.getSubjectId());
            assertThat(updated.getStatus()).isEqualTo(group.getStatus());
            assertThat(updated.getCreatedAt()).isEqualTo(group.getCreatedAt());
        }

        @Test
        @DisplayName("should trim code on changeData")
        void shouldTrimCodeOnChangeData() {
            Group group = createActiveGroup();
            Group updated = group.changeData(
                    PERIOD_ID, BRANCH_ID, TEACHER_ID,
                    "  MATH-202  ", 30, LATER
            );
            assertThat(updated.getCode()).isEqualTo("MATH-202");
        }

        @Test
        @DisplayName("should reject blank code on changeData")
        void shouldRejectBlankCodeOnChangeData() {
            Group group = createActiveGroup();
            assertThatThrownBy(() -> group.changeData(
                    PERIOD_ID, BRANCH_ID, TEACHER_ID,
                    "  ", 30, LATER
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("code");
        }

        @Test
        @DisplayName("should reject capacity <= 0 on changeData")
        void shouldRejectInvalidCapacityOnChangeData() {
            Group group = createActiveGroup();
            assertThatThrownBy(() -> group.changeData(
                    PERIOD_ID, BRANCH_ID, TEACHER_ID,
                    "MATH-202", 0, LATER
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("capacity");
        }
    }

    @Nested
    @DisplayName("Group.changeSchedules")
    class GroupChangeSchedulesTests {

        @Test
        @DisplayName("should replace entire schedule list")
        void shouldReplaceEntireScheduleList() {
            Group group = createActiveGroup();
            Schedule newSchedule = createSchedule("TUESDAY", 14, 16);

            Group updated = group.changeSchedules(List.of(newSchedule), LATER);

            assertThat(updated.getSchedules()).hasSize(1);
            assertThat(updated.getSchedules().get(0).dayOfWeek()).isEqualTo("TUESDAY");
            assertThat(updated.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("should allow empty schedule list")
        void shouldAllowEmptyScheduleList() {
            Group group = Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30,
                    List.of(createValidSchedule()), FIXED_NOW
            );

            Group updated = group.changeSchedules(List.of(), LATER);
            assertThat(updated.getSchedules()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Group.deactivate")
    class GroupDeactivateTests {

        @Test
        @DisplayName("should deactivate ACTIVE group")
        void shouldDeactivateActiveGroup() {
            Group group = createActiveGroup();
            Group deactivated = group.deactivate(LATER);

            assertThat(deactivated.getStatus()).isEqualTo(GroupStatus.INACTIVE);
            assertThat(deactivated.getUpdatedAt()).isEqualTo(LATER);
            // Other fields preserved
            assertThat(deactivated.getId()).isEqualTo(group.getId());
            assertThat(deactivated.getSchedules()).isEqualTo(group.getSchedules());
        }

        @Test
        @DisplayName("should be idempotent on already INACTIVE group")
        void shouldBeIdempotentOnAlreadyInactive() {
            Group inactive = Group.restore(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, List.of(),
                    GroupStatus.INACTIVE, 0L, FIXED_NOW, FIXED_NOW
            );

            Group result = inactive.deactivate(LATER);
            assertThat(result.getStatus()).isEqualTo(GroupStatus.INACTIVE);
            // Idempotent: returns same state
            assertThat(result).isEqualTo(inactive);
        }
    }

    @Nested
    @DisplayName("Group equality")
    class GroupEqualityTests {

        @Test
        @DisplayName("should equal when same ID")
        void shouldEqualWhenSameId() {
            Group g1 = createActiveGroup();
            Group g2 = Group.restore(
                    g1.getId(), g1.getSchoolId(), g1.getSubjectId(),
                    g1.getAcademicPeriodId(), g1.getBranchId(),
                    g1.getTeacherId(), g1.getCode(), g1.getCapacity(),
                    g1.getSchedules(), g1.getStatus(), g1.getVersion(),
                    g1.getCreatedAt(), g1.getUpdatedAt()
            );
            assertThat(g1).isEqualTo(g2);
        }

        @Test
        @DisplayName("should not equal when different ID")
        void shouldNotEqualWhenDifferentId() {
            Group g1 = createActiveGroup();
            Group g2 = Group.create(
                    GroupId.generate(), g1.getSchoolId(), g1.getSubjectId(),
                    g1.getAcademicPeriodId(), g1.getBranchId(),
                    g1.getTeacherId(), "OTHER", 30, List.of(), FIXED_NOW
            );
            assertThat(g1).isNotEqualTo(g2);
        }
    }

    // ======================== helpers ========================

    private Group createActiveGroup() {
        return Group.create(
                GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                TEACHER_ID, "MATH-101", 30,
                List.of(createValidSchedule()), FIXED_NOW
        );
    }
}
