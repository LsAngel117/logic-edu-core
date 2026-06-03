package com.logossystemsit.logiceducore.application.academic.structure.usecase;

import com.logossystemsit.logiceducore.application.academic.structure.port.in.GetAcademicStructureUseCase;
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

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAcademicStructureService")
class GetAcademicStructureServiceTest {

    @Mock
    private AcademicStructureRepository repository;

    private GetAcademicStructureUseCase useCase;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final AcademicStructureId STRUCTURE_ID = new AcademicStructureId("660e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        useCase = new GetAcademicStructureService(repository);
    }

    @Test
    @DisplayName("should find structure by id")
    void shouldFindStructureById() {
        AcademicStructure structure = AcademicStructure.create(
                STRUCTURE_ID, SCHOOL_ID, StructureType.TRIMESTRAL,
                12, 3, 2, 7, 50, NOW
        );
        when(repository.findById(STRUCTURE_ID)).thenReturn(Optional.of(structure));

        var result = useCase.execute(STRUCTURE_ID);

        assertThat(result.id()).isEqualTo(STRUCTURE_ID.value());
        assertThat(result.structureType()).isEqualTo("TRIMESTRAL");
    }

    @Test
    @DisplayName("should throw when structure not found by id")
    void shouldThrowWhenStructureNotFoundById() {
        when(repository.findById(STRUCTURE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(STRUCTURE_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("should find active structure by school id")
    void shouldFindActiveStructureBySchoolId() {
        AcademicStructure structure = AcademicStructure.create(
                STRUCTURE_ID, SCHOOL_ID, StructureType.ANUAL,
                1, 3, 0, 5, 40, NOW
        );
        when(repository.findActiveBySchoolId(SCHOOL_ID)).thenReturn(Optional.of(structure));

        var result = useCase.findActiveBySchoolId(SCHOOL_ID);

        assertThat(result.schoolId()).isEqualTo(SCHOOL_ID.value());
        assertThat(result.active()).isTrue();
    }

    @Test
    @DisplayName("should throw when no active structure for school")
    void shouldThrowWhenNoActiveStructureForSchool() {
        when(repository.findActiveBySchoolId(SCHOOL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.findActiveBySchoolId(SCHOOL_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No active AcademicStructure found");
    }
}
