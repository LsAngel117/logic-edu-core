package com.logossystemsit.logiceducore.application.academic.attendance.usecase;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.RegisterAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.command.UpdateAttendanceCommand;
import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.application.academic.attendance.port.in.*;
import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.Attendance;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject.AttendanceId;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject.AttendanceStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Attendance application services")
class AttendanceServicesTest {

    @Mock private AttendanceRepository attendanceRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private Clock clock;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final UserId TEACHER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final UserId OTHER_TEACHER_ID = new UserId("880e8400-e29b-41d4-a716-446655440003");
    private static final UserId STUDENT_ID = new UserId("770e8400-e29b-41d4-a716-446655440002");
    private static final LocalDate ATTENDANCE_DATE = LocalDate.of(2026, 6, 1);
    private static final AttendanceId ATTENDANCE_ID = AttendanceId.generate();

    private Attendance sampleAttendance;
    private Group sampleGroup;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_NOW);

        sampleAttendance = Attendance.create(
                ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                AttendanceStatus.PRESENT, null, FIXED_NOW
        );

        sampleGroup = Group.restore(
                GROUP_ID,
                new com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId("550e8400-e29b-41d4-a716-446655440000"),
                new SubjectId("660e8400-e29b-41d4-a716-446655440001"),
                new AcademicPeriodId("770e8400-e29b-41d4-a716-446655440002"),
                com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId.of("880e8400-e29b-41d4-a716-446655440003"),
                TEACHER_ID, "MATH-101", 30, List.of(),
                GroupStatus.ACTIVE, 0L, FIXED_NOW, FIXED_NOW
        );
    }

    // ======================== RegisterAttendanceService ========================

    @Nested
    @DisplayName("RegisterAttendanceService")
    class RegisterAttendanceServiceTests {

        private RegisterAttendanceUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new RegisterAttendanceService(attendanceRepository, groupRepository, clock);
        }

        @Test
        @DisplayName("should register attendance successfully")
        void shouldRegisterAttendanceSuccessfully() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            RegisterAttendanceCommand command = new RegisterAttendanceCommand(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID,
                    AttendanceStatus.PRESENT, null, TEACHER_ID
            );

            AttendanceResult result = useCase.execute(command);

            assertThat(result.groupId()).isEqualTo(GROUP_ID.value());
            assertThat(result.studentId()).isEqualTo(STUDENT_ID.value());
            assertThat(result.status()).isEqualTo("PRESENT");
            verify(attendanceRepository).save(any());
        }

        @Test
        @DisplayName("should register attendance with observations")
        void shouldRegisterAttendanceWithObservations() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            RegisterAttendanceCommand command = new RegisterAttendanceCommand(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID,
                    AttendanceStatus.LATE, "Arrived 15 min late", TEACHER_ID
            );

            AttendanceResult result = useCase.execute(command);

            assertThat(result.status()).isEqualTo("LATE");
            assertThat(result.observations()).isEqualTo("Arrived 15 min late");
        }

        @Test
        @DisplayName("should throw 403 when teacher is not the group owner")
        void shouldThrow403WhenTeacherIsNotOwner() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            RegisterAttendanceCommand command = new RegisterAttendanceCommand(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID,
                    AttendanceStatus.PRESENT, null, OTHER_TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should throw 422 when group not found")
        void shouldThrow422WhenGroupNotFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.empty());

            RegisterAttendanceCommand command = new RegisterAttendanceCommand(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID,
                    AttendanceStatus.PRESENT, null, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Group not found");
        }

        @Test
        @DisplayName("should throw 422 when group is inactive")
        void shouldThrow422WhenGroupIsInactive() {
            Group inactiveGroup = Group.restore(
                    GROUP_ID,
                    new com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId("550e8400-e29b-41d4-a716-446655440000"),
                    new SubjectId("660e8400-e29b-41d4-a716-446655440001"),
                    new AcademicPeriodId("770e8400-e29b-41d4-a716-446655440002"),
                    com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId.of("880e8400-e29b-41d4-a716-446655440003"),
                    TEACHER_ID, "MATH-101", 30, List.of(),
                    GroupStatus.INACTIVE, 0L, FIXED_NOW, FIXED_NOW
            );
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(inactiveGroup));

            RegisterAttendanceCommand command = new RegisterAttendanceCommand(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID,
                    AttendanceStatus.PRESENT, null, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not active");
        }
    }

    // ======================== GetAttendanceByDateService ========================

    @Nested
    @DisplayName("GetAttendanceByDateService")
    class GetAttendanceByDateServiceTests {

        private GetAttendanceByDateUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new GetAttendanceByDateService(attendanceRepository);
        }

        @Test
        @DisplayName("should return attendances for date")
        void shouldReturnAttendancesForDate() {
            when(attendanceRepository.findByGroupIdAndDate(GROUP_ID, ATTENDANCE_DATE))
                    .thenReturn(List.of(sampleAttendance));

            List<AttendanceResult> results = useCase.execute(GROUP_ID, ATTENDANCE_DATE);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).status()).isEqualTo("PRESENT");
        }

        @Test
        @DisplayName("should return empty list when no attendances for date")
        void shouldReturnEmptyListWhenNoAttendances() {
            when(attendanceRepository.findByGroupIdAndDate(GROUP_ID, ATTENDANCE_DATE))
                    .thenReturn(List.of());

            List<AttendanceResult> results = useCase.execute(GROUP_ID, ATTENDANCE_DATE);

            assertThat(results).isEmpty();
        }
    }

    // ======================== ListAttendancesByGroupService ========================

    @Nested
    @DisplayName("ListAttendancesByGroupService")
    class ListAttendancesByGroupServiceTests {

        private ListAttendancesByGroupUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ListAttendancesByGroupService(attendanceRepository);
        }

        @Test
        @DisplayName("should list all attendances for group")
        void shouldListAllAttendancesForGroup() {
            when(attendanceRepository.findByGroupId(GROUP_ID))
                    .thenReturn(List.of(sampleAttendance));

            List<AttendanceResult> results = useCase.execute(GROUP_ID);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).id()).isEqualTo(ATTENDANCE_ID.value());
        }

        @Test
        @DisplayName("should return empty list when no attendances")
        void shouldReturnEmptyListWhenNoAttendances() {
            when(attendanceRepository.findByGroupId(GROUP_ID)).thenReturn(List.of());

            List<AttendanceResult> results = useCase.execute(GROUP_ID);

            assertThat(results).isEmpty();
        }
    }

    // ======================== UpdateAttendanceService ========================

    @Nested
    @DisplayName("UpdateAttendanceService")
    class UpdateAttendanceServiceTests {

        private UpdateAttendanceUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateAttendanceService(attendanceRepository, groupRepository, clock);
        }

        @Test
        @DisplayName("should update attendance status when owner teacher")
        void shouldUpdateWhenOwnerTeacher() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(attendanceRepository.findByGroupIdAndDateAndStudentId(GROUP_ID, ATTENDANCE_DATE, STUDENT_ID))
                    .thenReturn(Optional.of(sampleAttendance));

            UpdateAttendanceCommand command = new UpdateAttendanceCommand(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID,
                    AttendanceStatus.ABSENT, "Left early", TEACHER_ID
            );

            AttendanceResult result = useCase.execute(command);

            assertThat(result.status()).isEqualTo("ABSENT");
            assertThat(result.observations()).isEqualTo("Left early");
            verify(attendanceRepository).save(any());
        }

        @Test
        @DisplayName("should throw 403 when teacher is not owner")
        void shouldThrow403WhenTeacherNotOwner() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            UpdateAttendanceCommand command = new UpdateAttendanceCommand(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID,
                    AttendanceStatus.ABSENT, null, OTHER_TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should throw 404 when attendance not found")
        void shouldThrow404WhenAttendanceNotFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(attendanceRepository.findByGroupIdAndDateAndStudentId(GROUP_ID, ATTENDANCE_DATE, STUDENT_ID))
                    .thenReturn(Optional.empty());

            UpdateAttendanceCommand command = new UpdateAttendanceCommand(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID,
                    AttendanceStatus.ABSENT, null, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Attendance not found");
        }
    }
}
