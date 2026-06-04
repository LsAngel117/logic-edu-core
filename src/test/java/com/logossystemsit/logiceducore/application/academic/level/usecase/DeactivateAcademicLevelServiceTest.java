package com.logossystemsit.logiceducore.application.academic.level.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.DeactivateAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelStatus;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeactivateAcademicLevelService")
class DeactivateAcademicLevelServiceTest {

    @Mock
    private AcademicLevelRepository repository;

    @Mock
    private Clock clock;

    private DeactivateAcademicLevelUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new DeactivateAcademicLevelService(repository, clock);
    }

    @Test
    @DisplayName("should deactivate level with no active periods")
    void shouldDeactivateLevelWithNoActivePeriods() {
        AcademicLevel level = AcademicLevel.create(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1, FIXED_NOW
        );
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.of(level));
        when(repository.existsActivePeriodsByLevelId(LEVEL_ID)).thenReturn(false);
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

        AcademicLevelResult result = useCase.execute(LEVEL_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("should throw when level not found")
    void shouldThrowWhenLevelNotFound() {
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(LEVEL_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("should throw when level already inactive")
    void shouldThrowWhenLevelAlreadyInactive() {
        AcademicLevel inactive = AcademicLevel.restore(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1,
                AcademicLevelStatus.INACTIVE, FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> useCase.execute(LEVEL_ID))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already inactive");
    }

    @Test
    @DisplayName("should throw when level has active periods")
    void shouldThrowWhenLevelHasActivePeriods() {
        AcademicLevel level = AcademicLevel.create(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1, FIXED_NOW
        );
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.of(level));
        when(repository.existsActivePeriodsByLevelId(LEVEL_ID)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(LEVEL_ID))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot deactivate level with active");
    }
}
