package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.command.CreateAcademicLevelCommand;
import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.CreateAcademicLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAcademicLevelService")
class CreateAcademicLevelServiceTest {

    @Mock
    private AcademicLevelRepository repository;

    @Mock
    private Clock clock;

    private CreateAcademicLevelUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new CreateAcademicLevelService(repository, clock);
    }

    @Test
    @DisplayName("should create level and return result when number is unique")
    void shouldCreateLevelWhenNumberIsUnique() {
        when(clock.instant()).thenReturn(FIXED_NOW);
        when(repository.existsBySchoolIdAndNumber(SCHOOL_ID, 1)).thenReturn(false);

        CreateAcademicLevelCommand command = new CreateAcademicLevelCommand(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1
        );

        AcademicLevelResult result = useCase.execute(command);

        assertThat(result.id()).isEqualTo(LEVEL_ID.value());
        assertThat(result.schoolId()).isEqualTo(SCHOOL_ID.value());
        assertThat(result.name()).isEqualTo("Primaria");
        assertThat(result.number()).isEqualTo(1);
        assertThat(result.status()).isEqualTo("ACTIVE");

        ArgumentCaptor<AcademicLevel> captor = ArgumentCaptor.forClass(AcademicLevel.class);
        verify(repository).save(captor.capture());
        AcademicLevel saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(LEVEL_ID);
        assertThat(saved.getStatus().name()).isEqualTo("ACTIVE");
        assertThat(saved.getName()).isEqualTo("Primaria");
        assertThat(saved.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("should throw when number already exists in school")
    void shouldThrowWhenNumberAlreadyExists() {
        when(repository.existsBySchoolIdAndNumber(SCHOOL_ID, 1)).thenReturn(true);

        CreateAcademicLevelCommand command = new CreateAcademicLevelCommand(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should create level with different number and school")
    void shouldCreateLevelWithDifferentNumberAndSchool() {
        SchoolId otherSchool = new SchoolId("880e8400-e29b-41d4-a716-446655440003");
        when(clock.instant()).thenReturn(FIXED_NOW);
        when(repository.existsBySchoolIdAndNumber(otherSchool, 5)).thenReturn(false);

        CreateAcademicLevelCommand command = new CreateAcademicLevelCommand(
                LEVEL_ID, otherSchool, "Bachillerato", 5
        );

        AcademicLevelResult result = useCase.execute(command);

        assertThat(result.name()).isEqualTo("Bachillerato");
        assertThat(result.number()).isEqualTo(5);
        verify(repository).save(any());
    }
}
