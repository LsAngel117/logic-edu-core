package com.logossystemsit.logiceducore.application.academic.group.usecase;

import com.logossystemsit.logiceducore.application.academic.group.dto.command.CreateGroupCommand;
import com.logossystemsit.logiceducore.application.academic.group.dto.command.ScheduleData;
import com.logossystemsit.logiceducore.application.academic.group.dto.result.GroupResult;
import com.logossystemsit.logiceducore.application.academic.group.port.in.CreateGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.*;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectStatus;
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
import org.mockito.ArgumentCaptor;
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
@DisplayName("CreateGroupService")
class CreateGroupServiceTest {

    @Mock private GroupRepository groupRepository;
    @Mock private SchoolRepository schoolRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private AcademicPeriodRepository periodRepository;
    @Mock private BranchRepository branchRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private Clock clock;

    // Domain mocks pre-created to avoid nested stubbing
    @Mock private School mockSchool;
    @Mock private Subject mockSubject;
    @Mock private AcademicPeriod mockPeriod;
    @Mock private Branch mockBranch;
    @Mock private Membership mockMembership;

    private CreateGroupUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final SubjectId SUBJECT_ID = new SubjectId("660e8400-e29b-41d4-a716-446655440001");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("770e8400-e29b-41d4-a716-446655440002");
    private static final BranchId BRANCH_ID = BranchId.of("880e8400-e29b-41d4-a716-446655440003");
    private static final UserId TEACHER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");

    @BeforeEach
    void setUp() {
        useCase = new CreateGroupService(
                groupRepository, schoolRepository, subjectRepository,
                periodRepository, branchRepository, membershipRepository, clock
        );
        // Default: all domain entities are active
        when(mockSchool.isActive()).thenReturn(true);
        when(mockSubject.getStatus()).thenReturn(SubjectStatus.ACTIVE);
        when(mockPeriod.getStatus()).thenReturn(PeriodStatus.ACTIVE);
        when(mockBranch.isActive()).thenReturn(true);
        when(mockMembership.isActive()).thenReturn(true);
        when(mockMembership.isTeacher()).thenReturn(true);
    }

    @Nested
    @DisplayName("successful creation")
    class SuccessfulCreation {

        @Test
        @DisplayName("should create group when all validations pass")
        void shouldCreateGroupWhenAllValidationsPass() {
            setupAllValidations();
            CreateGroupCommand command = createValidCommand();

            GroupResult result = useCase.execute(command);

            assertThat(result.code()).isEqualTo("MATH-101");
            assertThat(result.capacity()).isEqualTo(30);
            assertThat(result.status()).isEqualTo("ACTIVE");
            assertThat(result.schedules()).hasSize(1);
            assertThat(result.schedules().get(0).dayOfWeek()).isEqualTo("MONDAY");

            ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
            verify(groupRepository).save(captor.capture());
            Group saved = captor.getValue();
            assertThat(saved.getStatus()).isEqualTo(GroupStatus.ACTIVE);
            assertThat(saved.getCapacity()).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("school validation")
    class SchoolValidation {

        @Test
        @DisplayName("should throw when school not found")
        void shouldThrowWhenSchoolNotFound() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.empty());
            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("School not found");
        }

        @Test
        @DisplayName("should throw when school is inactive")
        void shouldThrowWhenSchoolIsInactive() {
            when(mockSchool.isActive()).thenReturn(false);
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(mockSchool));

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("School is not active");
        }
    }

    @Nested
    @DisplayName("subject validation")
    class SubjectValidation {

        @Test
        @DisplayName("should throw when subject not found")
        void shouldThrowWhenSubjectNotFound() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.empty());

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Subject not found");
        }

        @Test
        @DisplayName("should throw when subject is inactive")
        void shouldThrowWhenSubjectIsInactive() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(mockSubject.getStatus()).thenReturn(SubjectStatus.INACTIVE);
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(mockSubject));

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Subject is not active");
        }
    }

    @Nested
    @DisplayName("period validation")
    class PeriodValidation {

        @Test
        @DisplayName("should throw when academic period not found")
        void shouldThrowWhenPeriodNotFound() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.empty());

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Academic period not found");
        }

        @Test
        @DisplayName("should throw when academic period is inactive")
        void shouldThrowWhenPeriodIsInactive() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
            when(mockPeriod.getStatus()).thenReturn(PeriodStatus.INACTIVE);
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.of(mockPeriod));

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Academic period is not active");
        }
    }

    @Nested
    @DisplayName("branch validation")
    class BranchValidation {

        @Test
        @DisplayName("should throw when branch not found")
        void shouldThrowWhenBranchNotFound() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
            when(periodRepository.findById(PERIOD_ID)).thenReturn(activePeriod());
            when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Branch not found");
        }

        @Test
        @DisplayName("should throw when branch is inactive")
        void shouldThrowWhenBranchIsInactive() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
            when(periodRepository.findById(PERIOD_ID)).thenReturn(activePeriod());
            when(mockBranch.isActive()).thenReturn(false);
            when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(mockBranch));

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Branch is not active");
        }
    }

    @Nested
    @DisplayName("teacher validation")
    class TeacherValidation {

        @Test
        @DisplayName("should throw when teacher has no memberships")
        void shouldThrowWhenTeacherHasNoMemberships() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
            when(periodRepository.findById(PERIOD_ID)).thenReturn(activePeriod());
            when(branchRepository.findById(BRANCH_ID)).thenReturn(activeBranch());
            when(membershipRepository.findByUserId(TEACHER_ID)).thenReturn(List.of());

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Teacher does not have TEACHER role");
        }

        @Test
        @DisplayName("should throw when teacher has no active TEACHER role")
        void shouldThrowWhenTeacherHasNoActiveTeacherRole() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
            when(periodRepository.findById(PERIOD_ID)).thenReturn(activePeriod());
            when(branchRepository.findById(BRANCH_ID)).thenReturn(activeBranch());
            when(mockMembership.isTeacher()).thenReturn(false);
            when(membershipRepository.findByUserId(TEACHER_ID)).thenReturn(List.of(mockMembership));

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Teacher does not have TEACHER role");
        }
    }

    @Nested
    @DisplayName("code validation")
    class CodeValidation {

        @Test
        @DisplayName("should throw when code already exists in school")
        void shouldThrowWhenCodeAlreadyExists() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
            when(periodRepository.findById(PERIOD_ID)).thenReturn(activePeriod());
            when(branchRepository.findById(BRANCH_ID)).thenReturn(activeBranch());
            when(mockMembership.isTeacher()).thenReturn(true);
            when(membershipRepository.findByUserId(TEACHER_ID)).thenReturn(List.of(mockMembership));
            when(groupRepository.existsBySchoolIdAndCode(SCHOOL_ID, "MATH-101")).thenReturn(true);

            CreateGroupCommand command = createValidCommand();

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Group code MATH-101 already exists");
        }
    }

    @Nested
    @DisplayName("capacity validation")
    class CapacityValidation {

        @Test
        @DisplayName("should throw when capacity is zero")
        void shouldThrowWhenCapacityIsZero() {
            when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
            when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
            when(periodRepository.findById(PERIOD_ID)).thenReturn(activePeriod());
            when(branchRepository.findById(BRANCH_ID)).thenReturn(activeBranch());
            when(mockMembership.isTeacher()).thenReturn(true);
            when(membershipRepository.findByUserId(TEACHER_ID)).thenReturn(List.of(mockMembership));
            when(groupRepository.existsBySchoolIdAndCode(SCHOOL_ID, "MATH-101")).thenReturn(false);

            ScheduleData schedule = new ScheduleData("MONDAY", LocalTime.of(8, 0), LocalTime.of(10, 0), "Room 101");
            CreateGroupCommand command = new CreateGroupCommand(
                    SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                    TEACHER_ID, "MATH-101", 0, List.of(schedule)
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("capacity");
        }
    }

    // ======================== helpers ========================

    private CreateGroupCommand createValidCommand() {
        ScheduleData schedule = new ScheduleData("MONDAY", LocalTime.of(8, 0), LocalTime.of(10, 0), "Room 101");
        return new CreateGroupCommand(
                SCHOOL_ID, SUBJECT_ID, PERIOD_ID, BRANCH_ID,
                TEACHER_ID, "MATH-101", 30, List.of(schedule)
        );
    }

    private void setupAllValidations() {
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(activeSchool());
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(activeSubject());
        when(periodRepository.findById(PERIOD_ID)).thenReturn(activePeriod());
        when(branchRepository.findById(BRANCH_ID)).thenReturn(activeBranch());
        when(mockMembership.isTeacher()).thenReturn(true);
        when(membershipRepository.findByUserId(TEACHER_ID)).thenReturn(List.of(mockMembership));
        when(groupRepository.existsBySchoolIdAndCode(SCHOOL_ID, "MATH-101")).thenReturn(false);
        when(clock.instant()).thenReturn(FIXED_NOW);
    }

    private Optional<School> activeSchool() {
        return Optional.of(mockSchool);
    }

    private Optional<Subject> activeSubject() {
        return Optional.of(mockSubject);
    }

    private Optional<AcademicPeriod> activePeriod() {
        return Optional.of(mockPeriod);
    }

    private Optional<Branch> activeBranch() {
        return Optional.of(mockBranch);
    }
}
