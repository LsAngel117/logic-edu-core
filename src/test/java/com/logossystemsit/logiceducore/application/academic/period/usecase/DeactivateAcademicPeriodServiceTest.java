package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.DeactivateAcademicPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.*;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeactivateAcademicPeriodService")
class DeactivateAcademicPeriodServiceTest {

    @Mock
    private AcademicPeriodRepository repository;

    @Mock
    private Clock clock;

    private DeactivateAcademicPeriodUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("990e8400-e29b-41d4-a716-446655440004");

    @BeforeEach
    void setUp() {
        useCase = new DeactivateAcademicPeriodService(repository, clock);
    }

    @Test
    @DisplayName("should deactivate period with no active evaluation periods")
    void shouldDeactivatePeriod() {
        AcademicPeriod period = AcademicPeriod.create(
                PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                "Primer Semestre", 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31),
                FIXED_NOW
        );
        when(repository.findById(PERIOD_ID)).thenReturn(Optional.of(period));
        when(repository.existsActiveEvaluationPeriodsByPeriodId(PERIOD_ID)).thenReturn(false);
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

        AcademicPeriodResult result = useCase.execute(PERIOD_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        assertThat(result.id()).isEqualTo(PERIOD_ID.value());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("should reject deactivation when active evaluation periods exist")
    void shouldRejectWhenActiveEvaluationPeriodsExist() {
        AcademicPeriod period = AcademicPeriod.create(
                PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                "Primer Semestre", 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31),
                FIXED_NOW
        );
        when(repository.findById(PERIOD_ID)).thenReturn(Optional.of(period));
        when(repository.existsActiveEvaluationPeriodsByPeriodId(PERIOD_ID)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(PERIOD_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("active evaluation periods");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should throw when already inactive")
    void shouldThrowWhenAlreadyInactive() {
        AcademicPeriod inactive = AcademicPeriod.restore(
                PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                "Primer Semestre", 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31),
                PeriodStatus.INACTIVE, FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(PERIOD_ID)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> useCase.execute(PERIOD_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already inactive");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should throw when period not found")
    void shouldThrowWhenPeriodNotFound() {
        when(repository.findById(PERIOD_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(PERIOD_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
