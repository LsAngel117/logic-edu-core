package com.logossystemsit.logiceducore.application.academic.structure.usecase;

import com.logossystemsit.logiceducore.application.academic.structure.dto.command.UpdateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.port.in.UpdateAcademicStructureUseCase;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.StructureType;
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
@DisplayName("UpdateAcademicStructureService")
class UpdateAcademicStructureServiceTest {

    @Mock
    private AcademicStructureRepository repository;

    @Mock
    private Clock clock;

    private UpdateAcademicStructureUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final AcademicStructureId STRUCTURE_ID = new AcademicStructureId("660e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        useCase = new UpdateAcademicStructureService(repository, clock);
    }

    @Test
    @DisplayName("should deactivate current and create new version")
    void shouldDeactivateCurrentAndCreateNewVersion() {
        when(clock.instant()).thenReturn(FIXED_NOW);

        AcademicStructure v1 = AcademicStructure.create(
                STRUCTURE_ID, SCHOOL_ID, StructureType.SEMESTRAL,
                10, 2, 3, 6, 45, FIXED_NOW
        );
        when(repository.findById(STRUCTURE_ID)).thenReturn(Optional.of(v1));

        UpdateAcademicStructureCommand command = new UpdateAcademicStructureCommand(
                STRUCTURE_ID, SCHOOL_ID, StructureType.ANUAL,
                12, 3, 2, 8, 50
        );

        var result = useCase.execute(command);

        // Verify old structure was deactivated and saved
        verify(repository, times(2)).save(any());
        assertThat(result.version()).isEqualTo(2);
        assertThat(result.structureType()).isEqualTo("ANUAL");
        assertThat(result.levelsCount()).isEqualTo(12);
        assertThat(result.active()).isTrue();
    }

    @Test
    @DisplayName("should throw when structure not found")
    void shouldThrowWhenStructureNotFound() {
        when(repository.findById(STRUCTURE_ID)).thenReturn(Optional.empty());

        UpdateAcademicStructureCommand command = new UpdateAcademicStructureCommand(
                STRUCTURE_ID, SCHOOL_ID, StructureType.ANUAL,
                12, 3, 2, 8, 50
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should throw when structure is already inactive")
    void shouldThrowWhenStructureIsAlreadyInactive() {
        AcademicStructure inactive = AcademicStructure.restore(
                STRUCTURE_ID, SCHOOL_ID, StructureType.SEMESTRAL,
                10, 2, 3, 6, 45,
                1, false, FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(STRUCTURE_ID)).thenReturn(Optional.of(inactive));

        UpdateAcademicStructureCommand command = new UpdateAcademicStructureCommand(
                STRUCTURE_ID, SCHOOL_ID, StructureType.ANUAL,
                12, 3, 2, 8, 50
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot modify an inactive structure");

        verify(repository, never()).save(any());
    }
}
