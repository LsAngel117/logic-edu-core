package com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.*;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.Schedule;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.ScheduleId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity.GroupEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity.GroupScheduleEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.repository.GroupJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GroupRepositoryAdapter")
class GroupRepositoryAdapterTest {

    @Mock
    private GroupJpaRepository jpa;

    private GroupRepository adapter;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final SubjectId SUBJECT_ID = new SubjectId("660e8400-e29b-41d4-a716-446655440001");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("770e8400-e29b-41d4-a716-446655440002");
    private static final BranchId BRANCH_ID = BranchId.of("880e8400-e29b-41d4-a716-446655440003");
    private static final UserId TEACHER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");

    @BeforeEach
    void setUp() {
        adapter = new GroupRepositoryAdapter(jpa);
    }

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("should map group to entity and save")
        void shouldMapGroupToEntityAndSave() {
            Schedule schedule1 = new Schedule(
                    ScheduleId.generate(), "MONDAY",
                    LocalTime.of(8, 0), LocalTime.of(10, 0), "Room 101"
            );
            Schedule schedule2 = new Schedule(
                    ScheduleId.generate(), "WEDNESDAY",
                    LocalTime.of(14, 0), LocalTime.of(16, 0), null
            );

            Group group = Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30,
                    List.of(schedule1, schedule2), FIXED_NOW
            );

            adapter.save(group);

            ArgumentCaptor<GroupEntity> captor = ArgumentCaptor.forClass(GroupEntity.class);
            verify(jpa).save(captor.capture());
            GroupEntity entity = captor.getValue();

            assertThat(entity.getId()).isEqualTo(GROUP_ID.value());
            assertThat(entity.getSchoolId()).isEqualTo(SCHOOL_ID.value());
            assertThat(entity.getSubjectId()).isEqualTo(SUBJECT_ID.value());
            assertThat(entity.getAcademicPeriodId()).isEqualTo(PERIOD_ID.value());
            assertThat(entity.getBranchId()).isEqualTo(BRANCH_ID.value());
            assertThat(entity.getTeacherId()).isEqualTo(TEACHER_ID.value());
            assertThat(entity.getCode()).isEqualTo("MATH-101");
            assertThat(entity.getCapacity()).isEqualTo(30);
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
            assertThat(entity.getVersion()).isEqualTo(0L);
            assertThat(entity.getSchedules()).hasSize(2);
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_NOW);

            // Verify schedules are mapped correctly
            GroupScheduleEntity s1 = entity.getSchedules().get(0);
            assertThat(s1.getDayOfWeek()).isEqualTo("MONDAY");
            assertThat(s1.getStartTime()).isEqualTo(LocalTime.of(8, 0));
            assertThat(s1.getEndTime()).isEqualTo(LocalTime.of(10, 0));
            assertThat(s1.getClassroom()).isEqualTo("Room 101");
            assertThat(s1.getGroup()).isSameAs(entity);
        }

        @Test
        @DisplayName("should save group with no schedules")
        void shouldSaveGroupWithNoSchedules() {
            Group group = Group.create(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, List.of(), FIXED_NOW
            );

            adapter.save(group);

            ArgumentCaptor<GroupEntity> captor = ArgumentCaptor.forClass(GroupEntity.class);
            verify(jpa).save(captor.capture());
            assertThat(captor.getValue().getSchedules()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("should find group by ID and map to domain")
        void shouldFindGroupByIdAndMapToDomain() {
            GroupEntity entity = createSampleEntity();
            when(jpa.findById(GROUP_ID.value())).thenReturn(Optional.of(entity));

            Optional<Group> result = adapter.findById(GROUP_ID);

            assertThat(result).isPresent();
            Group group = result.get();
            assertThat(group.getId()).isEqualTo(GROUP_ID);
            assertThat(group.getCode()).isEqualTo("MATH-101");
            assertThat(group.getCapacity()).isEqualTo(30);
            assertThat(group.getStatus()).isEqualTo(GroupStatus.ACTIVE);
            assertThat(group.getVersion()).isEqualTo(0L);
            assertThat(group.getSchedules()).hasSize(2);
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(jpa.findById("nonexistent")).thenReturn(Optional.empty());

            Optional<Group> result = adapter.findById(new GroupId("nonexistent"));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySchoolId")
    class FindBySchoolIdTests {

        @Test
        @DisplayName("should find groups by school ID")
        void shouldFindGroupsBySchoolId() {
            GroupEntity entity = createSampleEntity();
            when(jpa.findBySchoolId(SCHOOL_ID.value())).thenReturn(List.of(entity));

            List<Group> result = adapter.findBySchoolId(SCHOOL_ID);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCode()).isEqualTo("MATH-101");
        }
    }

    @Nested
    @DisplayName("findBySchoolIdAndBranchId")
    class FindBySchoolIdAndBranchIdTests {

        @Test
        @DisplayName("should find groups with school and branch filter")
        void shouldFindGroupsWithSchoolAndBranchFilter() {
            GroupEntity entity = createSampleEntity();
            when(jpa.findBySchoolIdAndBranchId(SCHOOL_ID.value(), BRANCH_ID.value()))
                    .thenReturn(List.of(entity));

            List<Group> result = adapter.findBySchoolIdAndBranchId(SCHOOL_ID, BRANCH_ID);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findBySchoolIdAndPeriodId")
    class FindBySchoolIdAndPeriodIdTests {

        @Test
        @DisplayName("should find groups with school and period filter")
        void shouldFindGroupsWithSchoolAndPeriodFilter() {
            GroupEntity entity = createSampleEntity();
            when(jpa.findBySchoolIdAndAcademicPeriodId(SCHOOL_ID.value(), PERIOD_ID.value()))
                    .thenReturn(List.of(entity));

            List<Group> result = adapter.findBySchoolIdAndPeriodId(SCHOOL_ID, PERIOD_ID);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("existsBySchoolIdAndCode")
    class ExistsBySchoolIdAndCodeTests {

        @Test
        @DisplayName("should return true when code exists")
        void shouldReturnTrueWhenCodeExists() {
            when(jpa.existsBySchoolIdAndCode(SCHOOL_ID.value(), "MATH-101")).thenReturn(true);

            assertThat(adapter.existsBySchoolIdAndCode(SCHOOL_ID, "MATH-101")).isTrue();
        }

        @Test
        @DisplayName("should return false when code does not exist")
        void shouldReturnFalseWhenCodeDoesNotExist() {
            when(jpa.existsBySchoolIdAndCode(SCHOOL_ID.value(), "NONEXISTENT")).thenReturn(false);

            assertThat(adapter.existsBySchoolIdAndCode(SCHOOL_ID, "NONEXISTENT")).isFalse();
        }
    }

    // ======================== helpers ========================

    private GroupEntity createSampleEntity() {
        GroupEntity entity = new GroupEntity();
        entity.setId(GROUP_ID.value());
        entity.setSchoolId(SCHOOL_ID.value());
        entity.setSubjectId(SUBJECT_ID.value());
        entity.setAcademicPeriodId(PERIOD_ID.value());
        entity.setBranchId(BRANCH_ID.value());
        entity.setTeacherId(TEACHER_ID.value());
        entity.setCode("MATH-101");
        entity.setCapacity(30);
        entity.setStatus("ACTIVE");
        entity.setVersion(0L);
        entity.setCreatedAt(FIXED_NOW);
        entity.setUpdatedAt(FIXED_NOW);

        GroupScheduleEntity s1 = new GroupScheduleEntity();
        s1.setId(ScheduleId.generate().value());
        s1.setDayOfWeek("MONDAY");
        s1.setStartTime(LocalTime.of(8, 0));
        s1.setEndTime(LocalTime.of(10, 0));
        s1.setClassroom("Room 101");
        s1.setGroup(entity);

        GroupScheduleEntity s2 = new GroupScheduleEntity();
        s2.setId(ScheduleId.generate().value());
        s2.setDayOfWeek("WEDNESDAY");
        s2.setStartTime(LocalTime.of(14, 0));
        s2.setEndTime(LocalTime.of(16, 0));
        s2.setGroup(entity);

        entity.setSchedules(List.of(s1, s2));
        return entity;
    }
}
