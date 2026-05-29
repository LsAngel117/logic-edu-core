package com.logossystemsit.logiceducore.application.academic.evaluation.usecase;

import com.logossystemsit.logiceducore.application.academic.evaluation.dto.result.EvaluationPeriodResult;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.in.ListEvaluationPeriodsByPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListEvaluationPeriodsByPeriodService")
class ListEvaluationPeriodsByPeriodServiceTest {

    @Mock
    private EvaluationPeriodRepository repository;

    private ListEvaluationPeriodsByPeriodUseCase useCase;

    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("period-1");
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        useCase = new ListEvaluationPeriodsByPeriodService(repository);
    }

    @Nested
    @DisplayName("happy path")
    class HappyPath {

        @Test
        @DisplayName("should return list of evaluation periods")
        void shouldReturnList() {
            EvaluationPeriod ep1 = EvaluationPeriod.restore(
                    new EvaluationPeriodId("eval-1"), PERIOD_ID,
                    "Examen 1", 1, new BigDecimal("30.00"),
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15),
                    EvaluationPeriodStatus.ACTIVE, NOW, NOW
            );
            EvaluationPeriod ep2 = EvaluationPeriod.restore(
                    new EvaluationPeriodId("eval-2"), PERIOD_ID,
                    "Examen 2", 2, new BigDecimal("40.00"),
                    LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 15),
                    EvaluationPeriodStatus.ACTIVE, NOW, NOW
            );
            when(repository.findByPeriodId(PERIOD_ID)).thenReturn(List.of(ep1, ep2));

            List<EvaluationPeriodResult> results = useCase.execute(PERIOD_ID);

            assertThat(results).hasSize(2);
            assertThat(results.get(0).id()).isEqualTo("eval-1");
            assertThat(results.get(1).id()).isEqualTo("eval-2");
        }

        @Test
        @DisplayName("should return empty list when no periods")
        void shouldReturnEmptyList() {
            when(repository.findByPeriodId(PERIOD_ID)).thenReturn(List.of());

            List<EvaluationPeriodResult> results = useCase.execute(PERIOD_ID);

            assertThat(results).isEmpty();
        }
    }
}
