package com.logossystemsit.logiceducore.application.academic.grade.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.grade.dto.command.RegisterGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.command.UpdateGradeCommand;
import com.logossystemsit.logiceducore.application.academic.grade.dto.result.GradeResult;
import com.logossystemsit.logiceducore.application.academic.grade.port.in.*;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentType;
import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;
import com.logossystemsit.logiceducore.domain.academic.group.model.Group;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Grade application services")
class GradeServicesTest {

    @Mock private GradeRepository gradeRepository;
    @Mock private AssessmentRepository assessmentRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private UserRepository userRepository;
    @Mock private Clock clock;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final AssessmentId ASSESSMENT_ID = AssessmentId.generate();
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final UserId TEACHER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final UserId OTHER_TEACHER_ID = new UserId("880e8400-e29b-41d4-a716-446655440003");
    private static final UserId STUDENT_ID = new UserId("770e8400-e29b-41d4-a716-446655440002");
    private static final GradeId GRADE_ID = GradeId.generate();
    private static final BigDecimal MAX_SCORE = new BigDecimal("10.00");
    private static final BigDecimal VALID_VALUE = new BigDecimal("8.50");

    private Assessment sampleAssessment;
    private Group sampleGroup;
    private Grade sampleGrade;
    private User sampleStudent;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_NOW);

        sampleAssessment = Assessment.create(
                ASSESSMENT_ID, GROUP_ID, null,
                "Exam 1", AssessmentType.EXAM,
                new BigDecimal("1.00"), MAX_SCORE,
                FIXED_NOW
        );

        sampleGroup = Group.restore(
                GROUP_ID,
                new SchoolId("550e8400-e29b-41d4-a716-446655440000"),
                new SubjectId("660e8400-e29b-41d4-a716-446655440001"),
                new AcademicPeriodId("770e8400-e29b-41d4-a716-446655440002"),
                BranchId.of("880e8400-e29b-41d4-a716-446655440003"),
                TEACHER_ID, "MATH-101", 30, List.of(),
                GroupStatus.ACTIVE, 0L, FIXED_NOW, FIXED_NOW
        );

        sampleGrade = Grade.create(
                GRADE_ID, ASSESSMENT_ID, STUDENT_ID, VALID_VALUE, FIXED_NOW
        );

        sampleStudent = User.restore(
                STUDENT_ID,
                new Username("student1"),
                new Email("student@test.com"),
                new PasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUV"),
                new Name("Test", null, "Student", null),
                User.Sex.MALE,
                LocalDate.of(2000, 1, 1),
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                User.Status.ACTIVE,
                FIXED_NOW, FIXED_NOW
        );
    }

    // ======================== RegisterGradeService ========================

    @Nested
    @DisplayName("RegisterGradeService")
    class RegisterGradeServiceTests {

        private RegisterGradeUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new RegisterGradeService(
                    gradeRepository, assessmentRepository, groupRepository, userRepository, clock);
        }

        @Test
        @DisplayName("should register grade successfully")
        void shouldRegisterGradeSuccessfully() {
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(sampleAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(STUDENT_ID))
                    .thenReturn(Optional.of(sampleStudent));

            RegisterGradeCommand command = new RegisterGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, VALID_VALUE, TEACHER_ID
            );

            GradeResult result = useCase.execute(command);

            assertThat(result.assessmentId()).isEqualTo(ASSESSMENT_ID.value());
            assertThat(result.studentId()).isEqualTo(STUDENT_ID.value());
            assertThat(result.value()).isEqualByComparingTo(VALID_VALUE);
            verify(gradeRepository).save(any());
        }

        @Test
        @DisplayName("should throw when assessment not found")
        void shouldThrowWhenAssessmentNotFound() {
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.empty());

            RegisterGradeCommand command = new RegisterGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, VALID_VALUE, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Assessment not found");
        }

        @Test
        @DisplayName("should throw when group not found")
        void shouldThrowWhenGroupNotFound() {
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(sampleAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.empty());

            RegisterGradeCommand command = new RegisterGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, VALID_VALUE, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Group not found");
        }

        @Test
        @DisplayName("should throw 403 when teacher is not the group owner")
        void shouldThrow403WhenTeacherIsNotOwner() {
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(sampleAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.of(sampleGroup));

            RegisterGradeCommand command = new RegisterGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, VALID_VALUE, OTHER_TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should throw when student not found")
        void shouldThrowWhenStudentNotFound() {
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(sampleAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(STUDENT_ID))
                    .thenReturn(Optional.empty());

            RegisterGradeCommand command = new RegisterGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, VALID_VALUE, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Student not found");
        }

        @Test
        @DisplayName("should throw when student is inactive")
        void shouldThrowWhenStudentIsInactive() {
            User inactiveStudent = User.restore(
                    STUDENT_ID,
                    new Username("student1"),
                    new Email("student@test.com"),
                    new PasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUV"),
                    new Name("Test", null, "Student", null),
                    User.Sex.MALE,
                    LocalDate.of(2000, 1, 1),
                    new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                    User.Status.INACTIVE,
                    FIXED_NOW, FIXED_NOW
            );

            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(sampleAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(STUDENT_ID))
                    .thenReturn(Optional.of(inactiveStudent));

            RegisterGradeCommand command = new RegisterGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, VALID_VALUE, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not active");
        }

        @Test
        @DisplayName("should throw 422 when value exceeds maxScore")
        void shouldThrow422WhenValueExceedsMaxScore() {
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(sampleAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.of(sampleGroup));
            when(userRepository.findById(STUDENT_ID))
                    .thenReturn(Optional.of(sampleStudent));

            RegisterGradeCommand command = new RegisterGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, new BigDecimal("15.00"), TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("exceeds max score");
        }
    }

    // ======================== GetGradeService ========================

    @Nested
    @DisplayName("GetGradeService")
    class GetGradeServiceTests {

        private GetGradeUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new GetGradeService(gradeRepository);
        }

        @Test
        @DisplayName("should return grade by ID")
        void shouldReturnGradeById() {
            when(gradeRepository.findById(GRADE_ID))
                    .thenReturn(Optional.of(sampleGrade));

            GradeResult result = useCase.execute(GRADE_ID);

            assertThat(result.id()).isEqualTo(GRADE_ID.value());
            assertThat(result.value()).isEqualByComparingTo(VALID_VALUE);
        }

        @Test
        @DisplayName("should throw when grade not found")
        void shouldThrowWhenGradeNotFound() {
            when(gradeRepository.findById(GRADE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(GRADE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Grade not found");
        }
    }

    // ======================== ListGradesByAssessmentService ========================

    @Nested
    @DisplayName("ListGradesByAssessmentService")
    class ListGradesByAssessmentServiceTests {

        private ListGradesByAssessmentUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ListGradesByAssessmentService(gradeRepository);
        }

        @Test
        @DisplayName("should list grades for assessment")
        void shouldListGradesForAssessment() {
            when(gradeRepository.findByAssessmentId(ASSESSMENT_ID))
                    .thenReturn(List.of(sampleGrade));

            List<GradeResult> results = useCase.execute(ASSESSMENT_ID);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).assessmentId()).isEqualTo(ASSESSMENT_ID.value());
        }

        @Test
        @DisplayName("should return empty list when no grades")
        void shouldReturnEmptyListWhenNoGrades() {
            when(gradeRepository.findByAssessmentId(ASSESSMENT_ID))
                    .thenReturn(List.of());

            List<GradeResult> results = useCase.execute(ASSESSMENT_ID);

            assertThat(results).isEmpty();
        }
    }

    // ======================== UpdateGradeService ========================

    @Nested
    @DisplayName("UpdateGradeService")
    class UpdateGradeServiceTests {

        private UpdateGradeUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateGradeService(
                    gradeRepository, assessmentRepository, groupRepository, clock);
        }

        @Test
        @DisplayName("should update grade value when owner teacher")
        void shouldUpdateWhenOwnerTeacher() {
            BigDecimal higherValue = new BigDecimal("11.00");
            Assessment higherMaxAssessment = Assessment.create(
                    ASSESSMENT_ID, GROUP_ID, null,
                    "Exam 1", AssessmentType.EXAM,
                    new BigDecimal("1.00"), higherValue,
                    FIXED_NOW
            );

            when(gradeRepository.findByAssessmentIdAndStudentId(ASSESSMENT_ID, STUDENT_ID))
                    .thenReturn(Optional.of(sampleGrade));
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(higherMaxAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.of(sampleGroup));

            UpdateGradeCommand command = new UpdateGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, new BigDecimal("9.50"), TEACHER_ID
            );

            GradeResult result = useCase.execute(command);

            assertThat(result.value()).isEqualByComparingTo(new BigDecimal("9.50"));
            verify(gradeRepository).save(any());
        }

        @Test
        @DisplayName("should throw 403 when teacher is not owner")
        void shouldThrow403WhenTeacherNotOwner() {
            when(gradeRepository.findByAssessmentIdAndStudentId(ASSESSMENT_ID, STUDENT_ID))
                    .thenReturn(Optional.of(sampleGrade));
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(sampleAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.of(sampleGroup));

            UpdateGradeCommand command = new UpdateGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, new BigDecimal("9.00"), OTHER_TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should throw 422 when value exceeds maxScore")
        void shouldThrow422WhenValueExceedsMaxScore() {
            when(gradeRepository.findByAssessmentIdAndStudentId(ASSESSMENT_ID, STUDENT_ID))
                    .thenReturn(Optional.of(sampleGrade));
            when(assessmentRepository.findById(ASSESSMENT_ID))
                    .thenReturn(Optional.of(sampleAssessment));
            when(groupRepository.findById(GROUP_ID))
                    .thenReturn(Optional.of(sampleGroup));

            UpdateGradeCommand command = new UpdateGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, new BigDecimal("15.00"), TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("exceeds max score");
        }

        @Test
        @DisplayName("should throw 404 when grade not found")
        void shouldThrow404WhenGradeNotFound() {
            when(gradeRepository.findByAssessmentIdAndStudentId(ASSESSMENT_ID, STUDENT_ID))
                    .thenReturn(Optional.empty());

            UpdateGradeCommand command = new UpdateGradeCommand(
                    ASSESSMENT_ID, STUDENT_ID, new BigDecimal("9.00"), TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Grade not found");
        }
    }
}
