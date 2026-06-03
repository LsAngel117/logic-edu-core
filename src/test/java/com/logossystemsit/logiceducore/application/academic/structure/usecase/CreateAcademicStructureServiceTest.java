package com.logossystemsit.logiceducore.application.academic.structure.usecase;

import com.logossystemsit.logiceducore.application.academic.structure.dto.command.CreateAcademicStructureCommand;
import com.logossystemsit.logiceducore.application.academic.structure.dto.result.AcademicStructureResult;
import com.logossystemsit.logiceducore.application.academic.structure.port.in.CreateAcademicStructureUseCase;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.StructureType;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAcademicStructureService")
class CreateAcademicStructureServiceTest {

    @Mock
    private AcademicStructureRepository repository;

    @Mock
    private Clock clock;

    private CreateAcademicStructureUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final AcademicStructureId STRUCTURE_ID = new AcademicStructureId("660e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        useCase = new CreateAcademicStructureService(repository, clock);
    }

    @Test
    @DisplayName("should create structure and return result when no active exists")
    void shouldCreateStructureWhenNoActiveExists() {
        when(clock.instant()).thenReturn(FIXED_NOW);
        when(repository.findActiveBySchoolId(SCHOOL_ID)).thenReturn(Optional.empty());

        CreateAcademicStructureCommand command = new CreateAcademicStructureCommand(
                STRUCTURE_ID, SCHOOL_ID, StructureType.SEMESTRAL,
                10, 2, 3, 6, 45
        );

        AcademicStructureResult result = useCase.execute(command);

        assertThat(result.id()).isEqualTo(STRUCTURE_ID.value());
        assertThat(result.schoolId()).isEqualTo(SCHOOL_ID.value());
        assertThat(result.structureType()).isEqualTo("SEMESTRAL");
        assertThat(result.levelsCount()).isEqualTo(10);
        assertThat(result.periodsPerLevel()).isEqualTo(2);
        assertThat(result.evaluationPeriodsPerPeriod()).isEqualTo(3);
        assertThat(result.subjectsPerPeriod()).isEqualTo(6);
        assertThat(result.hoursPerSubject()).isEqualTo(45);
        assertThat(result.active()).isTrue();
        assertThat(result.version()).isEqualTo(1);

        ArgumentCaptor<AcademicStructure> captor = ArgumentCaptor.forClass(AcademicStructure.class);
        verify(repository).save(captor.capture());
        AcademicStructure saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(STRUCTURE_ID);
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getVersion()).isEqualTo(1);
    }

    @Test
    @DisplayName("should throw when active structure already exists for school")
    void shouldThrowWhenActiveStructureAlreadyExists() {
        AcademicStructure existing = AcademicStructure.create(
                new AcademicStructureId("existing-id"),
                SCHOOL_ID,
                StructureType.ANUAL,
                3, 6, 4, 10, 50,
                FIXED_NOW
        );

        when(repository.findActiveBySchoolId(SCHOOL_ID)).thenReturn(Optional.of(existing));

        CreateAcademicStructureCommand command = new CreateAcademicStructureCommand(
                STRUCTURE_ID, SCHOOL_ID, StructureType.SEMESTRAL,
                10, 2, 3, 6, 45
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("active structure already exists");

        verify(repository, never()).save(any());
    }
}
