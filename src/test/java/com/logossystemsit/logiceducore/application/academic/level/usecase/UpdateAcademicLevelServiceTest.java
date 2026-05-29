package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.command.UpdateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.UpdateAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelStatus;
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
@DisplayName("UpdateAcademicLevelService")
class UpdateAcademicLevelServiceTest {

    @Mock
    private AcademicLevelRepository repository;

    @Mock
    private Clock clock;

    private UpdateAcademicLevelUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new UpdateAcademicLevelService(repository, clock);
    }

    @Test
    @DisplayName("should update name and number when no conflict")
    void shouldUpdateNameAndNumberWhenNoConflict() {
        AcademicLevel existing = AcademicLevel.create(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1, FIXED_NOW
        );
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.of(existing));
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));
        when(repository.existsBySchoolIdAndNumber(SCHOOL_ID, 2)).thenReturn(false);

        UpdateAcademicLevelCommand command = new UpdateAcademicLevelCommand(
                LEVEL_ID, SCHOOL_ID, "Educación Primaria", 2
        );

        AcademicLevelResult result = useCase.execute(command);

        assertThat(result.name()).isEqualTo("Educación Primaria");
        assertThat(result.number()).isEqualTo(2);
        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("should throw when level not found")
    void shouldThrowWhenLevelNotFound() {
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.empty());

        UpdateAcademicLevelCommand command = new UpdateAcademicLevelCommand(
                LEVEL_ID, SCHOOL_ID, "X", 1
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("should throw when level is inactive")
    void shouldThrowWhenLevelIsInactive() {
        AcademicLevel inactive = AcademicLevel.restore(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1,
                AcademicLevelStatus.INACTIVE, FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.of(inactive));

        UpdateAcademicLevelCommand command = new UpdateAcademicLevelCommand(
                LEVEL_ID, SCHOOL_ID, "X", 1
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("should throw when new number conflicts with another level in same school")
    void shouldThrowWhenNumberConflicts() {
        AcademicLevel existing = AcademicLevel.create(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1, FIXED_NOW
        );
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.of(existing));
        when(repository.existsBySchoolIdAndNumber(SCHOOL_ID, 2)).thenReturn(true);

        UpdateAcademicLevelCommand command = new UpdateAcademicLevelCommand(
                LEVEL_ID, SCHOOL_ID, "Primaria Avanzada", 2
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("should not check uniqueness when number unchanged")
    void shouldNotCheckUniquenessWhenNumberUnchanged() {
        AcademicLevel existing = AcademicLevel.create(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1, FIXED_NOW
        );
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.of(existing));
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

        UpdateAcademicLevelCommand command = new UpdateAcademicLevelCommand(
                LEVEL_ID, SCHOOL_ID, "Primaria Avanzada", 1
        );

        AcademicLevelResult result = useCase.execute(command);

        assertThat(result.name()).isEqualTo("Primaria Avanzada");
        assertThat(result.number()).isEqualTo(1);
        verify(repository, never()).existsBySchoolIdAndNumber(any(), anyInt());
    }
}
