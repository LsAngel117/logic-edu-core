package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.GetAcademicPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.period.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAcademicPeriodService")
class GetAcademicPeriodServiceTest {

    @Mock
    private AcademicPeriodRepository repository;

    private GetAcademicPeriodUseCase useCase;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("990e8400-e29b-41d4-a716-446655440004");

    @BeforeEach
    void setUp() {
        useCase = new GetAcademicPeriodService(repository);
    }

    @Test
    @DisplayName("should return period when found")
    void shouldReturnPeriodWhenFound() {
        AcademicPeriod period = AcademicPeriod.create(
                PERIOD_ID,
                new com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId("level-1"),
                PeriodType.SEMESTER,
                "Primer Semestre", 1,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 7, 31),
                NOW
        );
        when(repository.findById(PERIOD_ID)).thenReturn(Optional.of(period));

        AcademicPeriodResult result = useCase.execute(PERIOD_ID);

        assertThat(result.id()).isEqualTo(PERIOD_ID.value());
        assertThat(result.name()).isEqualTo("Primer Semestre");
        assertThat(result.periodType()).isEqualTo("SEMESTER");
    }

    @Test
    @DisplayName("should throw when period not found")
    void shouldThrowWhenNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(PERIOD_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
