package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.GetAcademicLevelUseCase;
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

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAcademicLevelService")
class GetAcademicLevelServiceTest {

    @Mock
    private AcademicLevelRepository repository;

    private GetAcademicLevelUseCase useCase;

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final AcademicLevelId LEVEL_ID = new AcademicLevelId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new GetAcademicLevelService(repository);
    }

    @Test
    @DisplayName("should return level result when found")
    void shouldReturnLevelResultWhenFound() {
        AcademicLevel level = AcademicLevel.create(
                LEVEL_ID, SCHOOL_ID, "Primaria", 1, NOW
        );
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.of(level));

        AcademicLevelResult result = useCase.execute(LEVEL_ID);

        assertThat(result.id()).isEqualTo(LEVEL_ID.value());
        assertThat(result.name()).isEqualTo("Primaria");
        assertThat(result.number()).isEqualTo(1);
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("should throw when level not found")
    void shouldThrowWhenLevelNotFound() {
        when(repository.findById(LEVEL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(LEVEL_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
