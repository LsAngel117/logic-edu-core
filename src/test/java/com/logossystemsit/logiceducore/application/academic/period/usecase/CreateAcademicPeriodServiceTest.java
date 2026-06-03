package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.command.CreateAcademicPeriodCommand;
import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.CreateAcademicPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.*;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodStatus;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.PeriodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAcademicPeriodService")
class CreateAcademicPeriodServiceTest {

    @Mock
    private AcademicPeriodRepository repository;

    @Mock
    private Clock clock;

    private CreateAcademicPeriodUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("990e8400-e29b-41d4-a716-446655440004");
    private static final LocalDate START = LocalDate.of(2026, 3, 1);
    private static final LocalDate END = LocalDate.of(2026, 7, 31);

    @BeforeEach
    void setUp() {
        useCase = new CreateAcademicPeriodService(repository, clock);
    }

    @Nested
    @DisplayName("happy path")
    class HappyPath {

        @Test
        @DisplayName("should create period when no overlap exists")
        void shouldCreatePeriodWhenNoOverlap() {
            when(clock.instant()).thenReturn(FIXED_NOW);
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of());

            CreateAcademicPeriodCommand command = new CreateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Primer Semestre", 1, START, END
            );

            AcademicPeriodResult result = useCase.execute(command);

            assertThat(result.id()).isEqualTo(PERIOD_ID.value());
            assertThat(result.levelId()).isEqualTo(LEVEL_ID.value());
            assertThat(result.name()).isEqualTo("Primer Semestre");
            assertThat(result.periodType()).isEqualTo("SEMESTER");
            assertThat(result.sequence()).isEqualTo(1);
            assertThat(result.status()).isEqualTo("ACTIVE");
            assertThat(result.startDate()).isEqualTo(START);
            assertThat(result.endDate()).isEqualTo(END);

            ArgumentCaptor<AcademicPeriod> captor = ArgumentCaptor.forClass(AcademicPeriod.class);
            verify(repository).save(captor.capture());
            AcademicPeriod saved = captor.getValue();
            assertThat(saved.getId()).isEqualTo(PERIOD_ID);
            assertThat(saved.getStatus()).isEqualTo(PeriodStatus.ACTIVE);
            assertThat(saved.getName()).isEqualTo("Primer Semestre");
        }

        @Test
        @DisplayName("should create period with different dates and type")
        void shouldCreatePeriodWithDifferentDates() {
            when(clock.instant()).thenReturn(FIXED_NOW);
            AcademicPeriod existing = AcademicPeriod.create(
                    new AcademicPeriodId("existing-1"), LEVEL_ID,
                    PeriodType.SEMESTER, "Period A", 1,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 28),
                    FIXED_NOW
            );
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of(existing));

            LocalDate newStart = LocalDate.of(2026, 3, 1);
            LocalDate newEnd = LocalDate.of(2026, 7, 31);
            CreateAcademicPeriodCommand command = new CreateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, PeriodType.TRIMESTER,
                    "Nuevo Trimestre", 2, newStart, newEnd
            );

            AcademicPeriodResult result = useCase.execute(command);

            assertThat(result.name()).isEqualTo("Nuevo Trimestre");
            assertThat(result.periodType()).isEqualTo("TRIMESTER");
            assertThat(result.sequence()).isEqualTo(2);
            verify(repository).save(any());
        }
    }

    @Nested
    @DisplayName("overlap validation")
    class OverlapValidation {

        @Test
        @DisplayName("should reject when new period starts before existing ends and ends after existing starts")
        void shouldRejectWhenOverlapping() {
            AcademicPeriod existing = AcademicPeriod.create(
                    new AcademicPeriodId("existing-1"), LEVEL_ID,
                    PeriodType.SEMESTER, "Period A", 1,
                    LocalDate.of(2026, 2, 1), LocalDate.of(2026, 6, 30),
                    FIXED_NOW
            );
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of(existing));

            CreateAcademicPeriodCommand command = new CreateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Overlapping Period", 2,
                    LocalDate.of(2026, 5, 1), LocalDate.of(2026, 8, 31)
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("overlap");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should reject when new period completely envelops existing")
        void shouldRejectWhenEnvelopingExisting() {
            AcademicPeriod existing = AcademicPeriod.create(
                    new AcademicPeriodId("existing-1"), LEVEL_ID,
                    PeriodType.SEMESTER, "Period A", 1,
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 6, 30),
                    FIXED_NOW
            );
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of(existing));

            CreateAcademicPeriodCommand command = new CreateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, PeriodType.ANUAL,
                    "Enveloping Period", 2,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("overlap");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should reject when new period is completely inside existing")
        void shouldRejectWhenInsideExisting() {
            AcademicPeriod existing = AcademicPeriod.create(
                    new AcademicPeriodId("existing-1"), LEVEL_ID,
                    PeriodType.ANUAL, "Annual", 1,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                    FIXED_NOW
            );
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of(existing));

            CreateAcademicPeriodCommand command = new CreateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Inside Period", 2,
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 6, 30)
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("overlap");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should allow when new period starts exactly at existing end date")
        void shouldAllowAdjacentStartAtEnd() {
            AcademicPeriod existing = AcademicPeriod.create(
                    new AcademicPeriodId("existing-1"), LEVEL_ID,
                    PeriodType.SEMESTER, "Period A", 1,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30),
                    FIXED_NOW
            );
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of(existing));
            when(clock.instant()).thenReturn(FIXED_NOW);

            CreateAcademicPeriodCommand command = new CreateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Second Period", 2,
                    LocalDate.of(2026, 6, 30), LocalDate.of(2026, 8, 31)
            );

            AcademicPeriodResult result = useCase.execute(command);

            assertThat(result.name()).isEqualTo("Second Period");
            assertThat(result.status()).isEqualTo("ACTIVE");
            verify(repository).save(any());
        }

        @Test
        @DisplayName("should allow when new period ends exactly at existing start date")
        void shouldAllowAdjacentEndAtStart() {
            AcademicPeriod existing = AcademicPeriod.create(
                    new AcademicPeriodId("existing-1"), LEVEL_ID,
                    PeriodType.SEMESTER, "Period A", 1,
                    LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31),
                    FIXED_NOW
            );
            when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of(existing));
            when(clock.instant()).thenReturn(FIXED_NOW);

            CreateAcademicPeriodCommand command = new CreateAcademicPeriodCommand(
                    PERIOD_ID, LEVEL_ID, PeriodType.SEMESTER,
                    "Previous Period", 2,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 1)
            );

            AcademicPeriodResult result = useCase.execute(command);

            assertThat(result.name()).isEqualTo("Previous Period");
            assertThat(result.status()).isEqualTo("ACTIVE");
            verify(repository).save(any());
        }
    }
}
