package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.CreateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.CreateEvaluationPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.*;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateEvaluationPeriodService")
class CreateEvaluationPeriodServiceTest {

    @Mock
    private EvaluationPeriodRepository evaluationRepository;

    @Mock
    private AcademicPeriodRepository periodRepository;

    @Mock
    private Clock clock;

    private CreateEvaluationPeriodUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("990e8400-e29b-41d4-a716-446655440004");
    private static final EvaluationPeriodId EVAL_ID = new EvaluationPeriodId("880e8400-e29b-41d4-a716-446655440003");
    private static final LocalDate START = LocalDate.of(2026, 3, 1);
    private static final LocalDate END = LocalDate.of(2026, 4, 15);

    @BeforeEach
    void setUp() {
        useCase = new CreateEvaluationPeriodService(evaluationRepository, periodRepository, clock);
    }

    @Nested
    @DisplayName("happy path")
    class HappyPath {

        @Test
        @DisplayName("should create evaluation period with valid weight")
        void shouldCreateWithValidWeight() {
            when(clock.instant()).thenReturn(FIXED_NOW);
            when(evaluationRepository.sumWeightsByPeriodId(PERIOD_ID)).thenReturn(BigDecimal.ZERO);
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.of(createPeriod()));

            CreateEvaluationPeriodCommand command = new CreateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, "Examen Parcial", 1,
                    new BigDecimal("30.00"), START, END
            );

            EvaluationPeriodResult result = useCase.execute(command);

            assertThat(result.id()).isEqualTo(EVAL_ID.value());
            assertThat(result.periodId()).isEqualTo(PERIOD_ID.value());
            assertThat(result.name()).isEqualTo("Examen Parcial");
            assertThat(result.weight()).isEqualByComparingTo(new BigDecimal("30.00"));
            assertThat(result.status()).isEqualTo("ACTIVE");

            ArgumentCaptor<EvaluationPeriod> captor = ArgumentCaptor.forClass(EvaluationPeriod.class);
            verify(evaluationRepository).save(captor.capture());
            assertThat(captor.getValue().getWeight()).isEqualByComparingTo(new BigDecimal("30.00"));
        }
    }

    @Nested
    @DisplayName("weight sum validation")
    class WeightSumValidation {

        @Test
        @DisplayName("should reject when total weight would exceed 100")
        void shouldRejectWhenTotalExceeds100() {
            when(evaluationRepository.sumWeightsByPeriodId(PERIOD_ID)).thenReturn(new BigDecimal("80.00"));
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.of(createPeriod()));

            CreateEvaluationPeriodCommand command = new CreateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, "Extra", 2,
                    new BigDecimal("30.00"), START, END
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
            verify(evaluationRepository, never()).save(any());
        }

        @Test
        @DisplayName("should reject when weight sum exactly exceeds 100")
        void shouldRejectWhenSumExactlyExceeds100() {
            when(evaluationRepository.sumWeightsByPeriodId(PERIOD_ID)).thenReturn(new BigDecimal("99.99"));
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.of(createPeriod()));

            CreateEvaluationPeriodCommand command = new CreateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, "Tiny", 2,
                    new BigDecimal("0.02"), START, END
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weight");
        }

        @Test
        @DisplayName("should accept when total weight equals exactly 100")
        void shouldAcceptWhenTotalExactly100() {
            when(clock.instant()).thenReturn(FIXED_NOW);
            when(evaluationRepository.sumWeightsByPeriodId(PERIOD_ID)).thenReturn(new BigDecimal("75.00"));
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.of(createPeriod()));

            CreateEvaluationPeriodCommand command = new CreateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, "Last", 2,
                    new BigDecimal("25.00"), START, END
            );

            EvaluationPeriodResult result = useCase.execute(command);

            assertThat(result.name()).isEqualTo("Last");
            verify(evaluationRepository).save(any());
        }
    }

    @Nested
    @DisplayName("period not found")
    class PeriodNotFound {

        @Test
        @DisplayName("should throw when period does not exist")
        void shouldThrowWhenPeriodNotFound() {
            when(periodRepository.findById(PERIOD_ID)).thenReturn(Optional.empty());

            CreateEvaluationPeriodCommand command = new CreateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, "Test", 1,
                    new BigDecimal("50.00"), START, END
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
            verify(evaluationRepository, never()).save(any());
        }
    }

    private AcademicPeriod createPeriod() {
        return AcademicPeriod.create(
                PERIOD_ID,
                new AcademicLevelId("level-1"),
                PeriodType.SEMESTER, "Semestre 1", 1,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30),
                FIXED_NOW
        );
    }
}
