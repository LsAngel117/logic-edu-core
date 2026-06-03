package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.command.UpdateEvaluationPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.UpdateEvaluationPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("UpdateEvaluationPeriodService")
class UpdateEvaluationPeriodServiceTest {

    @Mock
    private EvaluationPeriodRepository repository;

    @Mock
    private Clock clock;

    private UpdateEvaluationPeriodUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("period-1");
    private static final EvaluationPeriodId EVAL_ID = new EvaluationPeriodId("eval-1");

    @BeforeEach
    void setUp() {
        useCase = new UpdateEvaluationPeriodService(repository, clock);
    }

    @Nested
    @DisplayName("happy path")
    class HappyPath {

        @Test
        @DisplayName("should update name")
        void shouldUpdateName() {
            EvaluationPeriod existing = createActive();
            when(repository.findById(EVAL_ID)).thenReturn(Optional.of(existing));
            when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

            UpdateEvaluationPeriodCommand command = new UpdateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, "Nuevo Nombre", null, null, null, null
            );

            EvaluationPeriodResult result = useCase.execute(command);

            assertThat(result.name()).isEqualTo("Nuevo Nombre");
            assertThat(result.weight()).isEqualByComparingTo(new BigDecimal("25.00"));
            verify(repository).save(any());
        }

        @Test
        @DisplayName("should update weight")
        void shouldUpdateWeight() {
            EvaluationPeriod existing = createActive();
            when(repository.findById(EVAL_ID)).thenReturn(Optional.of(existing));
            when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));
            when(repository.sumWeightsByPeriodId(PERIOD_ID)).thenReturn(new BigDecimal("40.00"));

            UpdateEvaluationPeriodCommand command = new UpdateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, null, null, new BigDecimal("50.00"), null, null
            );

            EvaluationPeriodResult result = useCase.execute(command);

            assertThat(result.weight()).isEqualByComparingTo(new BigDecimal("50.00"));
        }
    }

    @Nested
    @DisplayName("not found")
    class NotFound {

        @Test
        @DisplayName("should throw when evaluation period not found")
        void shouldThrowWhenNotFound() {
            when(repository.findById(EVAL_ID)).thenReturn(Optional.empty());

            UpdateEvaluationPeriodCommand command = new UpdateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, "X", null, null, null, null
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("inactive guard")
    class InactiveGuard {

        @Test
        @DisplayName("should reject update on inactive period")
        void shouldRejectUpdateOnInactive() {
            EvaluationPeriod inactive = createInactive();
            when(repository.findById(EVAL_ID)).thenReturn(Optional.of(inactive));

            UpdateEvaluationPeriodCommand command = new UpdateEvaluationPeriodCommand(
                    EVAL_ID, PERIOD_ID, "New", null, null, null, null
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("inactive");
        }
    }

    private EvaluationPeriod createActive() {
        return EvaluationPeriod.restore(
                EVAL_ID, PERIOD_ID, "Original", 1, new BigDecimal("25.00"),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 15),
                EvaluationPeriodStatus.ACTIVE, FIXED_NOW, FIXED_NOW
        );
    }

    private EvaluationPeriod createInactive() {
        return EvaluationPeriod.restore(
                EVAL_ID, PERIOD_ID, "Old", 1, new BigDecimal("25.00"),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 15),
                EvaluationPeriodStatus.INACTIVE, FIXED_NOW, FIXED_NOW
        );
    }
}
