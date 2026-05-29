package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.ListAcademicPeriodsByLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.PeriodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListAcademicPeriodsByLevelService")
class ListAcademicPeriodsByLevelServiceTest {

    @Mock
    private AcademicPeriodRepository repository;

    private ListAcademicPeriodsByLevelUseCase useCase;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new ListAcademicPeriodsByLevelService(repository);
    }

    @Test
    @DisplayName("should return list of periods for level")
    void shouldReturnListOfPeriods() {
        AcademicPeriod p1 = AcademicPeriod.create(
                new AcademicPeriodId("per-1"), LEVEL_ID, PeriodType.SEMESTER,
                "Primer Semestre", 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31), NOW
        );
        AcademicPeriod p2 = AcademicPeriod.create(
                new AcademicPeriodId("per-2"), LEVEL_ID, PeriodType.SEMESTER,
                "Segundo Semestre", 2,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 11, 30), NOW
        );
        when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of(p1, p2));

        List<AcademicPeriodResult> results = useCase.execute(LEVEL_ID);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(AcademicPeriodResult::name)
                .containsExactly("Primer Semestre", "Segundo Semestre");
    }

    @Test
    @DisplayName("should return empty list when no periods for level")
    void shouldReturnEmptyListWhenNoPeriods() {
        when(repository.findByLevelId(LEVEL_ID)).thenReturn(List.of());

        List<AcademicPeriodResult> results = useCase.execute(LEVEL_ID);

        assertThat(results).isEmpty();
    }
}
