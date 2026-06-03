package com.logossystemsit.logiceducore.application.academic.enrollment.usecase;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.command.EnrollStudentCommand;
import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.EnrollStudentUseCase;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.valueobject.EnrollmentStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.*;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.User;
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
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EnrollStudentService")
class EnrollStudentServiceTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private UserRepository userRepository;
    @Mock private Clock clock;

    @Mock private User mockUser;

    private EnrollStudentUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final UserId USER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final GroupId GROUP_ID = new GroupId("771e8400-e29b-41d4-a716-446655440005");
    private static final SubjectId SUBJECT_ID = new SubjectId("661e8400-e29b-41d4-a716-446655440006");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("551e8400-e29b-41d4-a716-446655440007");

    private Group sampleGroup;

    @BeforeEach
    void setUp() {
        useCase = new EnrollStudentService(
                enrollmentRepository, groupRepository, userRepository, clock);
        when(clock.instant()).thenReturn(FIXED_NOW);

        sampleGroup = Group.restore(
                GROUP_ID,
                new SchoolId("550e8400-e29b-41d4-a716-446655440000"),
                SUBJECT_ID,
                PERIOD_ID,
                BranchId.of("880e8400-e29b-41d4-a716-446655440003"),
                new UserId("990e8400-e29b-41d4-a716-446655440009"),
                "MATH-101",
                30,
                List.of(),
                GroupStatus.ACTIVE,
                0L,
                FIXED_NOW,
                FIXED_NOW
        );
    }

    private EnrollStudentCommand validCommand() {
        return new EnrollStudentCommand(USER_ID, GROUP_ID);
    }

    private void setupValidEnroll() {
        when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(mockUser.isActive()).thenReturn(true);
        when(enrollmentRepository.existsByUserIdAndGroupId(USER_ID, GROUP_ID)).thenReturn(false);
        when(enrollmentRepository.existsActiveByStudentAndSubjectAndPeriod(USER_ID, SUBJECT_ID, PERIOD_ID))
                .thenReturn(false);
        when(enrollmentRepository.countActiveByGroupId(GROUP_ID)).thenReturn(10L);
    }

    @Nested
    @DisplayName("successful enrollment")
    class SuccessfulEnrollment {

        @Test
        @DisplayName("should create enrollment when all validations pass")
        void shouldCreateEnrollmentWhenAllValidationsPass() {
            setupValidEnroll();

            EnrollmentResult result = useCase.execute(validCommand());

            assertThat(result.userId()).isEqualTo(USER_ID.value());
            assertThat(result.groupId()).isEqualTo(GROUP_ID.value());
            assertThat(result.status()).isEqualTo(EnrollmentStatus.ACTIVE.name());
            assertThat(result.enrolledAt()).isEqualTo(FIXED_NOW);
            verify(enrollmentRepository).save(any());
            verify(groupRepository).save(sampleGroup);
        }
    }

    @Nested
    @DisplayName("group validation")
    class GroupValidation {

        @Test
        @DisplayName("should throw IllegalArgumentException when group not found")
        void shouldThrowWhenGroupNotFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(validCommand()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Group not found");
        }

        @Test
        @DisplayName("should throw IllegalStateException when group is INACTIVE")
        void shouldThrowWhenGroupInactive() {
            Group inactiveGroup = Group.restore(
                    GROUP_ID,
                    new SchoolId("550e8400-e29b-41d4-a716-446655440000"),
                    SUBJECT_ID,
                    PERIOD_ID,
                    BranchId.of("880e8400-e29b-41d4-a716-446655440003"),
                    new UserId("990e8400-e29b-41d4-a716-446655440009"),
                    "MATH-101",
                    30,
                    List.of(),
                    GroupStatus.INACTIVE,
                    0L,
                    FIXED_NOW,
                    FIXED_NOW
            );
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(inactiveGroup));

            assertThatThrownBy(() -> useCase.execute(validCommand()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("inactive");
        }
    }

    @Nested
    @DisplayName("student validation")
    class StudentValidation {

        @Test
        @DisplayName("should throw when student not found")
        void shouldThrowWhenStudentNotFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(validCommand()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Student not found");
        }

        @Test
        @DisplayName("should throw when student is not active")
        void shouldThrowWhenStudentNotActive() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(mockUser.isActive()).thenReturn(false);

            assertThatThrownBy(() -> useCase.execute(validCommand()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("active");
        }
    }

    @Nested
    @DisplayName("duplicate and cross-aggregate validation")
    class DuplicateAndCrossAggregateValidation {

        @Test
        @DisplayName("should throw when duplicate enrollment exists")
        void shouldThrowWhenDuplicateEnrollmentExists() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(mockUser.isActive()).thenReturn(true);
            when(enrollmentRepository.existsByUserIdAndGroupId(USER_ID, GROUP_ID)).thenReturn(true);

            assertThatThrownBy(() -> useCase.execute(validCommand()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already enrolled");
        }

        @Test
        @DisplayName("should throw when same student has active enrollment in same subject+period")
        void shouldThrowWhenSameSubjectAndPeriodConflict() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(mockUser.isActive()).thenReturn(true);
            when(enrollmentRepository.existsByUserIdAndGroupId(USER_ID, GROUP_ID)).thenReturn(false);
            when(enrollmentRepository.existsActiveByStudentAndSubjectAndPeriod(USER_ID, SUBJECT_ID, PERIOD_ID))
                    .thenReturn(true);

            assertThatThrownBy(() -> useCase.execute(validCommand()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("same subject");
        }
    }

    @Nested
    @DisplayName("capacity check")
    class CapacityCheck {

        @Test
        @DisplayName("should throw when group is full")
        void shouldThrowWhenGroupIsFull() {
            Group smallGroup = Group.restore(
                    GROUP_ID,
                    new SchoolId("550e8400-e29b-41d4-a716-446655440000"),
                    SUBJECT_ID,
                    PERIOD_ID,
                    BranchId.of("880e8400-e29b-41d4-a716-446655440003"),
                    new UserId("990e8400-e29b-41d4-a716-446655440009"),
                    "MATH-101",
                    5,
                    List.of(),
                    GroupStatus.ACTIVE,
                    0L,
                    FIXED_NOW,
                    FIXED_NOW
            );
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(smallGroup));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(mockUser.isActive()).thenReturn(true);
            when(enrollmentRepository.existsByUserIdAndGroupId(USER_ID, GROUP_ID)).thenReturn(false);
            when(enrollmentRepository.existsActiveByStudentAndSubjectAndPeriod(USER_ID, SUBJECT_ID, PERIOD_ID))
                    .thenReturn(false);
            when(enrollmentRepository.countActiveByGroupId(GROUP_ID)).thenReturn(5L);

            assertThatThrownBy(() -> useCase.execute(validCommand()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("capacity");
        }
    }

    @Nested
    @DisplayName("optimistic lock conflict")
    class OptimisticLockConflict {

        @Test
        @DisplayName("should throw when group version conflict on save")
        void shouldThrowWhenGroupVersionConflict() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
            when(mockUser.isActive()).thenReturn(true);
            when(enrollmentRepository.existsByUserIdAndGroupId(USER_ID, GROUP_ID)).thenReturn(false);
            when(enrollmentRepository.existsActiveByStudentAndSubjectAndPeriod(USER_ID, SUBJECT_ID, PERIOD_ID))
                    .thenReturn(false);
            when(enrollmentRepository.countActiveByGroupId(GROUP_ID)).thenReturn(10L);
            doThrow(new OptimisticLockingFailureException("version mismatch"))
                    .when(groupRepository).save(sampleGroup);

            assertThatThrownBy(() -> useCase.execute(validCommand()))
                    .isInstanceOf(OptimisticLockingFailureException.class);
        }
    }
}
