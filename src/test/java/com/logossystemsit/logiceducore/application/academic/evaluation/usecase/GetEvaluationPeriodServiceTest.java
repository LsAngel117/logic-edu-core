package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.GetEvaluationPeriodUseCase;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetEvaluationPeriodService")
class GetEvaluationPeriodServiceTest {

    @Mock
    private EvaluationPeriodRepository repository;

    private GetEvaluationPeriodUseCase useCase;

    private static final EvaluationPeriodId EVAL_ID = new EvaluationPeriodId("eval-1");
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        useCase = new GetEvaluationPeriodService(repository);
    }

    @Nested
    @DisplayName("happy path")
    class HappyPath {

        @Test
        @DisplayName("should return evaluation period by id")
        void shouldReturnById() {
            EvaluationPeriod period = EvaluationPeriod.restore(
                    EVAL_ID,
                    new AcademicPeriodId("period-1"),
                    "Examen", 1, new BigDecimal("25.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15),
                    EvaluationPeriodStatus.ACTIVE,
                    NOW, NOW
            );
            when(repository.findById(EVAL_ID)).thenReturn(Optional.of(period));

            EvaluationPeriodResult result = useCase.execute(EVAL_ID);

            assertThat(result.id()).isEqualTo("eval-1");
            assertThat(result.name()).isEqualTo("Examen");
            assertThat(result.weight()).isEqualByComparingTo(new BigDecimal("25.00"));
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
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }
}
