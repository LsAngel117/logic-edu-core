package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.command.UpdateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.UpdateAcademicPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
@DisplayName("UpdateAcademicPeriodService")
class UpdateAcademicPeriodServiceTest {

    @Mock
    private AcademicPeriodRepository repository;

    @Mock
    private Clock clock;

    private UpdateAcademicPeriodUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("990e8400-e29b-41d4-a716-446655440004");
    private static final LocalDate START = LocalDate.of(2026, 3, 1);
    private static final LocalDate END = LocalDate.of(2026, 7, 31);

    @BeforeEach
    void setUp() {
        useCase = new UpdateAcademicPeriodService(repository, clock);
    }

    @Nested
    @DisplayName("happy path")
    class HappyPath {

        @Test
        @DisplayName("should update name and save")
        void shouldUpdateName() {
            AcademicPeriod current = AcademicPeriod.create(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Primer Semestre", 1, START, END, FIXED_NOW
            );
            when(repository.findById(PERIOD_ID)).thenReturn(Optional.of(current));
            when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

            UpdateAcademicPeriodCommand command = new UpdateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, "Semestre 2026-I", null, null, null, null
            );

            AcademicPeriodResult result = useCase.execute(command);

            assertThat(result.name()).isEqualTo("Semestre 2026-I");
            assertThat(result.status()).isEqualTo("ACTIVE");
            verify(repository).save(any());
        }

        @Test
        @DisplayName("should update dates and re-validate overlap")
        void shouldUpdateDatesWithNoOverlap() {
            AcademicPeriod current = AcademicPeriod.create(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Primer Semestre", 1, START, END, FIXED_NOW
            );
            when(repository.findById(PERIOD_ID)).thenReturn(Optional.of(current));
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of());
            when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

            LocalDate newStart = LocalDate.of(2026, 4, 1);
            LocalDate newEnd = LocalDate.of(2026, 8, 15);
            UpdateAcademicPeriodCommand command = new UpdateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, null, null, null, newStart, newEnd
            );

            AcademicPeriodResult result = useCase.execute(command);

            assertThat(result.startDate()).isEqualTo(newStart);
            assertThat(result.endDate()).isEqualTo(newEnd);
            verify(repository).save(any());
        }
    }

    @Nested
    @DisplayName("overlap validation on update")
    class OverlapValidationOnUpdate {

        @Test
        @DisplayName("should reject when updated dates overlap with another period")
        void shouldRejectOverlappingUpdate() {
            AcademicPeriod current = AcademicPeriod.create(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Primer Semestre", 1, START, END, FIXED_NOW
            );
            AcademicPeriod other = AcademicPeriod.create(
                    new AcademicPeriodId("other-1"), LEVEL_ID, PeriodType.SEMESTER,
                    "Other Semestre", 2,
                    LocalDate.of(2026, 6, 1), LocalDate.of(2026, 8, 31),
                    FIXED_NOW
            );
            when(repository.findById(PERIOD_ID)).thenReturn(Optional.of(current));
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of(other));
            when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

            LocalDate newStart = LocalDate.of(2026, 5, 1);
            LocalDate newEnd = LocalDate.of(2026, 7, 31);
            UpdateAcademicPeriodCommand command = new UpdateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, null, null, null, newStart, newEnd
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("overlap");

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("inactive period")
    class InactivePeriod {

        @Test
        @DisplayName("should reject update of inactive period")
        void shouldRejectUpdateOfInactivePeriod() {
            AcademicPeriod inactive = AcademicPeriod.restore(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Primer Semestre", 1, START, END,
                    PeriodStatus.INACTIVE, FIXED_NOW, FIXED_NOW
            );
            when(repository.findById(PERIOD_ID)).thenReturn(Optional.of(inactive));

            UpdateAcademicPeriodCommand command = new UpdateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, "New Name", null, null, null, null
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("inactive");

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("not found")
    class NotFound {

        @Test
        @DisplayName("should throw when period not found")
        void shouldThrowWhenPeriodNotFound() {
            when(repository.findById(PERIOD_ID)).thenReturn(Optional.empty());

            UpdateAcademicPeriodCommand command = new UpdateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, "X", null, null, null, null
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }
}
