package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.DeactivateEvaluationPeriodUseCase;
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
@DisplayName("DeactivateEvaluationPeriodService")
class DeactivateEvaluationPeriodServiceTest {

    @Mock
    private EvaluationPeriodRepository repository;

    @Mock
    private Clock clock;

    private DeactivateEvaluationPeriodUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("period-1");
    private static final EvaluationPeriodId EVAL_ID = new EvaluationPeriodId("eval-1");

    @BeforeEach
    void setUp() {
        useCase = new DeactivateEvaluationPeriodService(repository, clock);
    }

    @Nested
    @DisplayName("happy path")
    class HappyPath {

        @Test
        @DisplayName("should deactivate an active evaluation period")
        void shouldDeactivateActive() {
            EvaluationPeriod active = EvaluationPeriod.restore(
                    EVAL_ID, PERIOD_ID, "Examen", 1, new BigDecimal("25.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 15),
                    EvaluationPeriodStatus.ACTIVE, FIXED_NOW, FIXED_NOW
            );
            when(repository.findById(EVAL_ID)).thenReturn(Optional.of(active));
            when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

            EvaluationPeriodResult result = useCase.execute(EVAL_ID);

            assertThat(result.status()).isEqualTo("INACTIVE");
            verify(repository).save(any());
        }
    }

    @Nested
    @DisplayName("already inactive")
    class AlreadyInactive {

        @Test
        @DisplayName("should throw when already inactive")
        void shouldThrowWhenAlreadyInactive() {
            EvaluationPeriod inactive = EvaluationPeriod.restore(
                    EVAL_ID, PERIOD_ID, "Examen", 1, new BigDecimal("25.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 15),
                    EvaluationPeriodStatus.INACTIVE, FIXED_NOW, FIXED_NOW
            );
            when(repository.findById(EVAL_ID)).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> useCase.execute(EVAL_ID))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("inactive");
        }
    }

    @Nested
    @DisplayName("not found")
    class NotFound {

        @Test
        @DisplayName("should throw when evaluation period not found")
        void shouldThrowWhenNotFound() {
            when(repository.findById(EVAL_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(EVAL_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");
        }
    }
}
