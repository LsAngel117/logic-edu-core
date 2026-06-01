package com.logossystemsit.logiceducore.application.academic.group.usecase;

import com.logossystemsit.logiceducore.application.academic.group.dto.command.ScheduleData;
import com.logossystemsit.logiceducore.application.academic.group.dto.command.UpdateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.*;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.*;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.PeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectStatus;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Group application services")
class GroupServicesTest {

    @Mock private GroupRepository groupRepository;
    @Mock private SchoolRepository schoolRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private AcademicPeriodRepository periodRepository;
    @Mock private BranchRepository branchRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private Clock clock;

    @Mock private School mockSchool;
    @Mock private Subject mockSubject;
    @Mock private AcademicPeriod mockPeriod;
    @Mock private Branch mockBranch;
    @Mock private Membership mockMembership;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final SubjectId SUBJECT_ID = new SubjectId("660e8400-e29b-41d4-a716-446655440001");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("770e8400-e29b-41d4-a716-446655440002");
    private static final BranchId BRANCH_ID = BranchId.of("880e8400-e29b-41d4-a716-446655440003");
    private static final UserId TEACHER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final GroupId GROUP_ID = GroupId.generate();

    private Group sampleGroup;

    @BeforeEach
    void setUp() {
        when(mockSchool.isActive()).thenReturn(true);
        when(mockSubject.getStatus()).thenReturn(SubjectStatus.ACTIVE);
        when(mockPeriod.getStatus()).thenReturn(PeriodStatus.ACTIVE);
        when(mockBranch.isActive()).thenReturn(true);
        when(mockMembership.isActive()).thenReturn(true);
        when(mockMembership.isTeacher()).thenReturn(true);
        when(clock.instant()).thenReturn(FIXED_NOW);

        sampleGroup = createSampleActiveGroup();
    }

    private Group createSampleActiveGroup() {
        Schedule schedule = new Schedule(
                ScheduleId.generate(), "MONDAY",
                LocalTime.of(8, 0), LocalTime.of(10, 0), "Room 101"
        );
        return Group.create(
                GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                TEACHER_ID, "MATH-101", 30, List.of(schedule), FIXED_NOW
        );
    }

    // ======================== GetGroupService ========================

    @Nested
    @DisplayName("GetGroupService")
    class GetGroupServiceTests {

        private GetGroupUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new GetGroupService(groupRepository);
        }

        @Test
        @DisplayName("should return group when found")
        void shouldReturnGroupWhenFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            GroupResult result = useCase.execute(GROUP_ID);

            assertThat(result.id()).isEqualTo(GROUP_ID.value());
            assertThat(result.code()).isEqualTo("MATH-101");
            assertThat(result.schedules()).hasSize(1);
        }

        @Test
        @DisplayName("should throw when group not found")
        void shouldThrowWhenGroupNotFound() {
            when(groupRepository.findById(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(GROUP_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Group not found");
        }
    }

    // ======================== ListGroupsBySchoolService ========================

    @Nested
    @DisplayName("ListGroupsBySchoolService")
    class ListGroupsBySchoolServiceTests {

        private ListGroupsBySchoolUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ListGroupsBySchoolService(groupRepository);
        }

        @Test
        @DisplayName("should list all groups by school")
        void shouldListAllGroupsBySchool() {
            when(groupRepository.findBySchoolId(SCHOOL_ID)).thenReturn(List.of(sampleGroup));

            List<GroupResult> results = useCase.execute(SCHOOL_ID, null, null);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).code()).isEqualTo("MATH-101");
        }

        @Test
        @DisplayName("should return empty list when no groups")
        void shouldReturnEmptyListWhenNoGroups() {
            when(groupRepository.findBySchoolId(SCHOOL_ID)).thenReturn(List.of());

            List<GroupResult> results = useCase.execute(SCHOOL_ID, null, null);

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("should filter by branch when branchId provided")
        void shouldFilterByBranchWhenBranchIdProvided() {
            when(groupRepository.findBySchoolIdAndBranchId(SCHOOL_ID, BRANCH_ID))
                    .thenReturn(List.of(sampleGroup));

            List<GroupResult> results = useCase.execute(SCHOOL_ID, BRANCH_ID, null);

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("should filter by period when periodId provided")
        void shouldFilterByPeriodWhenPeriodIdProvided() {
            when(groupRepository.findBySchoolIdAndPeriodId(SCHOOL_ID, PERIOD_ID))
                    .thenReturn(List.of(sampleGroup));

            List<GroupResult> results = useCase.execute(SCHOOL_ID, null, PERIOD_ID);

            assertThat(results).hasSize(1);
        }
    }

    // ======================== UpdateGroupService ========================

    @Nested
    @DisplayName("UpdateGroupService")
    class UpdateGroupServiceTests {

        private UpdateGroupUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateGroupService(
                    groupRepository, schoolRepository, subjectRepository,
                    periodRepository, branchRepository, membershipRepository, clock
            );
        }

        @Test
        @DisplayName("should update group data successfully")
        void shouldUpdateGroupDataSuccessfully() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(mockSchool));
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(mockSubject));
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.of(mockPeriod));
            when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(mockBranch));
            when(membershipRepository.findByUserId(TEACHER_ID)).thenReturn(List.of(mockMembership));
            when(groupRepository.existsBySchoolIdAndCode(SCHOOL_ID, "MATH-202")).thenReturn(false);

            UpdateGroupCommand command = new UpdateGroupCommand(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-202", 25
            );

            GroupResult result = useCase.execute(command);

            assertThat(result.code()).isEqualTo("MATH-202");
            assertThat(result.capacity()).isEqualTo(25);
            verify(groupRepository).save(any());
        }

        @Test
        @DisplayName("should throw when group is inactive")
        void shouldThrowWhenGroupIsInactive() {
            Group inactive = Group.restore(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, List.of(),
                    GroupStatus.INACTIVE, 0L, FIXED_NOW, FIXED_NOW
            );
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(inactive));

            UpdateGroupCommand command = new UpdateGroupCommand(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-202", 25
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot update an inactive group");
        }

        @Test
        @DisplayName("should throw when new code conflicts with another group")
        void shouldThrowWhenNewCodeConflicts() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(mockSchool));
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(mockSubject));
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.of(mockPeriod));
            when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(mockBranch));
            when(membershipRepository.findByUserId(TEACHER_ID)).thenReturn(List.of(mockMembership));
            when(groupRepository.existsBySchoolIdAndCode(SCHOOL_ID, "MATH-202")).thenReturn(true);

            UpdateGroupCommand command = new UpdateGroupCommand(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-202", 25
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    // ======================== UpdateGroupSchedulesService ========================

    @Nested
    @DisplayName("UpdateGroupSchedulesService")
    class UpdateGroupSchedulesServiceTests {

        private UpdateGroupSchedulesUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateGroupSchedulesService(groupRepository, clock);
        }

        @Test
        @DisplayName("should replace schedules successfully")
        void shouldReplaceSchedulesSuccessfully() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            List<ScheduleData> newSchedules = List.of(
                    new ScheduleData("TUESDAY", LocalTime.of(14, 0), LocalTime.of(16, 0), "Lab 2")
            );

            GroupResult result = useCase.execute(GROUP_ID, newSchedules);

            assertThat(result.schedules()).hasSize(1);
            assertThat(result.schedules().get(0).dayOfWeek()).isEqualTo("TUESDAY");
            verify(groupRepository).save(any());
        }

        @Test
        @DisplayName("should throw when group is inactive")
        void shouldThrowWhenGroupIsInactive() {
            Group inactive = Group.restore(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, List.of(),
                    GroupStatus.INACTIVE, 0L, FIXED_NOW, FIXED_NOW
            );
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> useCase.execute(GROUP_ID, List.of()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot update schedules of an inactive group");
        }

        @Test
        @DisplayName("should throw when group not found")
        void shouldThrowWhenGroupNotFound() {
            when(groupRepository.findById(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(GROUP_ID, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Group not found");
        }
    }

    // ======================== DeactivateGroupService ========================

    @Nested
    @DisplayName("DeactivateGroupService")
    class DeactivateGroupServiceTests {

        private DeactivateGroupUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DeactivateGroupService(groupRepository, clock);
        }

        @Test
        @DisplayName("should deactivate active group")
        void shouldDeactivateActiveGroup() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            GroupResult result = useCase.execute(GROUP_ID);

            assertThat(result.status()).isEqualTo("INACTIVE");
            verify(groupRepository).save(any());
        }

        @Test
        @DisplayName("should be idempotent on already inactive group")
        void shouldBeIdempotentOnAlreadyInactive() {
            Group inactive = Group.restore(
                    GROUP_ID, SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 30, List.of(),
                    GroupStatus.INACTIVE, 0L, FIXED_NOW, FIXED_NOW
            );
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(inactive));

            GroupResult result = useCase.execute(GROUP_ID);

            assertThat(result.status()).isEqualTo("INACTIVE");
            verify(groupRepository).save(any());
        }

        @Test
        @DisplayName("should throw when group not found")
        void shouldThrowWhenGroupNotFound() {
            when(groupRepository.findById(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(GROUP_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Group not found");
        }
    }
}
