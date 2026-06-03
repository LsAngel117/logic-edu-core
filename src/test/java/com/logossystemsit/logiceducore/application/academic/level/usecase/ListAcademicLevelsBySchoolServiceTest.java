package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.ListAcademicLevelsBySchoolUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListAcademicLevelsBySchoolService")
class ListAcademicLevelsBySchoolServiceTest {

    @Mock
    private AcademicLevelRepository repository;

    private ListAcademicLevelsBySchoolUseCase useCase;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");

    @BeforeEach
    void setUp() {
        useCase = new ListAcademicLevelsBySchoolService(repository);
    }

    @Test
    @DisplayName("should return list of levels for school")
    void shouldReturnListOfLevelsForSchool() {
        AcademicLevel level1 = AcademicLevel.create(
                AcademicLevelId.generate(), SCHOOL_ID, "Primaria", 1, NOW
        );
        AcademicLevel level2 = AcademicLevel.create(
                AcademicLevelId.generate(), SCHOOL_ID, "Secundaria", 2, NOW
        );
        when(repository.findAllBySchoolId(SCHOOL_ID)).thenReturn(List.of(level1, level2));

        List<AcademicLevelResult> results = useCase.execute(SCHOOL_ID);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).name()).isEqualTo("Primaria");
        assertThat(results.get(1).name()).isEqualTo("Secundaria");
    }

    @Test
    @DisplayName("should return empty list when no levels for school")
    void shouldReturnEmptyListWhenNoLevels() {
        when(repository.findAllBySchoolId(SCHOOL_ID)).thenReturn(List.of());

        List<AcademicLevelResult> results = useCase.execute(SCHOOL_ID);

        assertThat(results).isEmpty();
    }
}
