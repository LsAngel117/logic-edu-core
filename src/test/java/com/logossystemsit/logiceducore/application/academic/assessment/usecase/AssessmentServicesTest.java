package com.logossystemsit.logiceducore.application.academic.assessment.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.CreateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.command.UpdateAssessmentCommand;
import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.application.academic.assessment.port.in.*;
import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentType;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodStatus;
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

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Assessment application services")
class AssessmentServicesTest {

    @Mock private AssessmentRepository assessmentRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private GradeRepository gradeRepository;
    @Mock private EvaluationPeriodRepository evaluationPeriodRepository;
    @Mock private Clock clock;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final UserId TEACHER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final UserId OTHER_TEACHER_ID = new UserId("880e8400-e29b-41d4-a716-446655440003");
    private static final AssessmentId ASSESSMENT_ID = AssessmentId.generate();
    private static final EvaluationPeriodId EVAL_PERIOD_ID = EvaluationPeriodId.generate();

    private Assessment sampleAssessment;
    private Group sampleGroup;
    private EvaluationPeriod sampleEvalPeriod;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_NOW);

        sampleAssessment = Assessment.create(
                ASSESSMENT_ID, GROUP_ID, EVAL_PERIOD_ID,
                "Math Quiz 1", AssessmentType.QUIZ,
                new BigDecimal("15.00"), new BigDecimal("100.00"),
                FIXED_NOW
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

        sampleEvalPeriod = EvaluationPeriod.restore(
                EVAL_PERIOD_ID,
                new AcademicPeriodId("770e8400-e29b-41d4-a716-446655440002"),
                "P1", 1,
                new BigDecimal("30.00"),
                java.time.LocalDate.of(2026, 1, 1),
                java.time.LocalDate.of(2026, 6, 30),
                EvaluationPeriodStatus.ACTIVE,
                FIXED_NOW, FIXED_NOW
        );
    }

    // ======================== CreateAssessmentService ========================

    @Nested
    @DisplayName("CreateAssessmentService")
    class CreateAssessmentServiceTests {

        private CreateAssessmentUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new CreateAssessmentService(
                    assessmentRepository, groupRepository, evaluationPeriodRepository, clock);
        }

        @Test
        @DisplayName("should create assessment successfully")
        void shouldCreateAssessmentSuccessfully() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.existsByGroupIdAndName(GROUP_ID, "Math Quiz 1")).thenReturn(false);

            CreateAssessmentCommand command = new CreateAssessmentCommand(
                    GROUP_ID, "Math Quiz 1", AssessmentType.QUIZ,
                    new BigDecimal("15.00"), new BigDecimal("100.00"),
                    null, TEACHER_ID
            );

            AssessmentResult result = useCase.execute(command);

            assertThat(result.name()).isEqualTo("Math Quiz 1");
            assertThat(result.type()).isEqualTo("QUIZ");
            assertThat(result.weight()).isEqualByComparingTo("15.00");
            assertThat(result.groupId()).isEqualTo(GROUP_ID.value());
            verify(assessmentRepository).save(any());
        }

        @Test
        @DisplayName("should create assessment with evaluation period")
        void shouldCreateWithEvaluationPeriod() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.existsByGroupIdAndName(GROUP_ID, "Exam")).thenReturn(false);
            when(evaluationPeriodRepository.findById(EVAL_PERIOD_ID)).thenReturn(Optional.of(sampleEvalPeriod));

            CreateAssessmentCommand command = new CreateAssessmentCommand(
                    GROUP_ID, "Exam", AssessmentType.EXAM,
                    new BigDecimal("30.00"), new BigDecimal("100.00"),
                    EVAL_PERIOD_ID, TEACHER_ID
            );

            AssessmentResult result = useCase.execute(command);

            assertThat(result.evaluationPeriodId()).isEqualTo(EVAL_PERIOD_ID.value());
        }

        @Test
        @DisplayName("should throw 403 when teacher is not the group owner")
        void shouldThrow403WhenNotOwner() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            CreateAssessmentCommand command = new CreateAssessmentCommand(
                    GROUP_ID, "Math Quiz 1", AssessmentType.QUIZ,
                    new BigDecimal("15.00"), new BigDecimal("100.00"),
                    null, OTHER_TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should throw when group not found")
        void shouldThrowWhenGroupNotFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.empty());

            CreateAssessmentCommand command = new CreateAssessmentCommand(
                    GROUP_ID, "Math Quiz 1", AssessmentType.QUIZ,
                    new BigDecimal("15.00"), new BigDecimal("100.00"),
                    null, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Group not found");
        }

        @Test
        @DisplayName("should throw when group is inactive")
        void shouldThrowWhenGroupInactive() {
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

            CreateAssessmentCommand command = new CreateAssessmentCommand(
                    GROUP_ID, "Math Quiz 1", AssessmentType.QUIZ,
                    new BigDecimal("15.00"), new BigDecimal("100.00"),
                    null, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not active");
        }

        @Test
        @DisplayName("should throw when name already exists in group")
        void shouldThrowWhenNameExists() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.existsByGroupIdAndName(GROUP_ID, "Math Quiz 1")).thenReturn(true);

            CreateAssessmentCommand command = new CreateAssessmentCommand(
                    GROUP_ID, "Math Quiz 1", AssessmentType.QUIZ,
                    new BigDecimal("15.00"), new BigDecimal("100.00"),
                    null, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("already exists");
        }

        @Test
        @DisplayName("should throw when evaluation period not found")
        void shouldThrowWhenEvalPeriodNotFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.existsByGroupIdAndName(GROUP_ID, "Exam")).thenReturn(false);
            when(evaluationPeriodRepository.findById(EVAL_PERIOD_ID)).thenReturn(Optional.empty());

            CreateAssessmentCommand command = new CreateAssessmentCommand(
                    GROUP_ID, "Exam", AssessmentType.EXAM,
                    new BigDecimal("30.00"), new BigDecimal("100.00"),
                    EVAL_PERIOD_ID, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Evaluation period not found");
        }
    }

    // ======================== GetAssessmentService ========================

    @Nested
    @DisplayName("GetAssessmentService")
    class GetAssessmentServiceTests {

        private GetAssessmentUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new GetAssessmentService(assessmentRepository);
        }

        @Test
        @DisplayName("should find assessment by ID")
        void shouldFindById() {
            when(assessmentRepository.findById(ASSESSMENT_ID)).thenReturn(Optional.of(sampleAssessment));

            AssessmentResult result = useCase.execute(ASSESSMENT_ID);

            assertThat(result.id()).isEqualTo(ASSESSMENT_ID.value());
            assertThat(result.name()).isEqualTo("Math Quiz 1");
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowWhenNotFound() {
            when(assessmentRepository.findById(ASSESSMENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(ASSESSMENT_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Assessment not found");
        }
    }

    // ======================== ListAssessmentsByGroupService ========================

    @Nested
    @DisplayName("ListAssessmentsByGroupService")
    class ListAssessmentsByGroupServiceTests {

        private ListAssessmentsByGroupUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ListAssessmentsByGroupService(assessmentRepository);
        }

        @Test
        @DisplayName("should list assessments for group")
        void shouldListForGroup() {
            when(assessmentRepository.findByGroupId(GROUP_ID)).thenReturn(List.of(sampleAssessment));

            List<AssessmentResult> results = useCase.execute(GROUP_ID);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).id()).isEqualTo(ASSESSMENT_ID.value());
        }

        @Test
        @DisplayName("should return empty list when no assessments")
        void shouldReturnEmptyList() {
            when(assessmentRepository.findByGroupId(GROUP_ID)).thenReturn(List.of());

            List<AssessmentResult> results = useCase.execute(GROUP_ID);

            assertThat(results).isEmpty();
        }
    }

    // ======================== UpdateAssessmentService ========================

    @Nested
    @DisplayName("UpdateAssessmentService")
    class UpdateAssessmentServiceTests {

        private UpdateAssessmentUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateAssessmentService(assessmentRepository, groupRepository, clock);
        }

        @Test
        @DisplayName("should update assessment when owner teacher")
        void shouldUpdateWhenOwnerTeacher() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.findById(ASSESSMENT_ID)).thenReturn(Optional.of(sampleAssessment));

            UpdateAssessmentCommand command = new UpdateAssessmentCommand(
                    ASSESSMENT_ID, GROUP_ID,
                    "Updated Quiz", AssessmentType.EXAM,
                    new BigDecimal("20.00"), new BigDecimal("50.00"),
                    null, TEACHER_ID
            );

            AssessmentResult result = useCase.execute(command);

            assertThat(result.name()).isEqualTo("Updated Quiz");
            assertThat(result.type()).isEqualTo("EXAM");
            assertThat(result.weight()).isEqualByComparingTo("20.00");
            assertThat(result.maxScore()).isEqualByComparingTo("50.00");
            verify(assessmentRepository).save(any());
        }

        @Test
        @DisplayName("should throw 403 when teacher is not owner")
        void shouldThrow403WhenNotOwner() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            UpdateAssessmentCommand command = new UpdateAssessmentCommand(
                    ASSESSMENT_ID, GROUP_ID,
                    "Updated Quiz", AssessmentType.EXAM,
                    new BigDecimal("20.00"), new BigDecimal("50.00"),
                    null, OTHER_TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should throw when assessment not found")
        void shouldThrowWhenAssessmentNotFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.findById(ASSESSMENT_ID)).thenReturn(Optional.empty());

            UpdateAssessmentCommand command = new UpdateAssessmentCommand(
                    ASSESSMENT_ID, GROUP_ID,
                    "Updated Quiz", AssessmentType.EXAM,
                    new BigDecimal("20.00"), new BigDecimal("50.00"),
                    null, TEACHER_ID
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Assessment not found");
        }
    }

    // ======================== DeleteAssessmentService ========================

    @Nested
    @DisplayName("DeleteAssessmentService")
    class DeleteAssessmentServiceTests {

        private DeleteAssessmentUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DeleteAssessmentService(assessmentRepository, groupRepository, gradeRepository, clock);
        }

        @Test
        @DisplayName("should delete assessment when owner teacher")
        void shouldDeleteWhenOwnerTeacher() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.findById(ASSESSMENT_ID)).thenReturn(Optional.of(sampleAssessment));
            when(gradeRepository.countByAssessmentId(ASSESSMENT_ID)).thenReturn(0L);

            useCase.execute(ASSESSMENT_ID, GROUP_ID, TEACHER_ID);

            verify(assessmentRepository).delete(any());
        }

        @Test
        @DisplayName("should throw 409 when assessment has grades")
        void shouldThrow409WhenHasGrades() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.findById(ASSESSMENT_ID)).thenReturn(Optional.of(sampleAssessment));
            when(gradeRepository.countByAssessmentId(ASSESSMENT_ID)).thenReturn(3L);

            assertThatThrownBy(() -> useCase.execute(ASSESSMENT_ID, GROUP_ID, TEACHER_ID))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("has grades");
        }

        @Test
        @DisplayName("should throw 403 when teacher is not owner")
        void shouldThrow403WhenNotOwner() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));

            assertThatThrownBy(() -> useCase.execute(ASSESSMENT_ID, GROUP_ID, OTHER_TEACHER_ID))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should throw when assessment not found")
        void shouldThrowWhenAssessmentNotFound() {
            when(groupRepository.findById(GROUP_ID)).thenReturn(Optional.of(sampleGroup));
            when(assessmentRepository.findById(ASSESSMENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(ASSESSMENT_ID, GROUP_ID, TEACHER_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Assessment not found");
        }
    }
}
