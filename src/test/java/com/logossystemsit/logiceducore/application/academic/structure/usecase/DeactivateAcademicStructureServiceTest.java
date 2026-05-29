package com.logossystemsit.logiceducore.application.academic.structure.usecase;

import com.logossystemsit.logiceducore.application.academic.structure.port.in.DeactivateAcademicStructureUseCase;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.StructureType;
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
@DisplayName("DeactivateAcademicStructureService")
class DeactivateAcademicStructureServiceTest {

    @Mock
    private AcademicStructureRepository repository;

    @Mock
    private Clock clock;

    private DeactivateAcademicStructureUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final AcademicStructureId STRUCTURE_ID = new AcademicStructureId("660e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        useCase = new DeactivateAcademicStructureService(repository, clock);
    }

    @Test
    @DisplayName("should deactivate active structure")
    void shouldDeactivateActiveStructure() {
        when(clock.instant()).thenReturn(FIXED_NOW);

        AcademicStructure active = AcademicStructure.create(
                STRUCTURE_ID, SCHOOL_ID, StructureType.SEMESTRAL,
                10, 2, 3, 6, 45, FIXED_NOW
        );
        when(repository.findById(STRUCTURE_ID)).thenReturn(Optional.of(active));

        var result = useCase.execute(STRUCTURE_ID);

        assertThat(result.active()).isFalse();
        verify(repository).save(any());
    }

    @Test
    @DisplayName("should throw when structure not found")
    void shouldThrowWhenStructureNotFound() {
        when(repository.findById(STRUCTURE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(STRUCTURE_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should throw when structure already inactive")
    void shouldThrowWhenStructureAlreadyInactive() {
        AcademicStructure inactive = AcademicStructure.restore(
                STRUCTURE_ID, SCHOOL_ID, StructureType.SEMESTRAL,
                10, 2, 3, 6, 45,
                1, false, FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(STRUCTURE_ID)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> useCase.execute(STRUCTURE_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already inactive");

        verify(repository, never()).save(any());
    }
}
