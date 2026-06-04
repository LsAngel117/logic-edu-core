package com.logossystemsit.logiceducore.application.school.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.DeactivateSchoolUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
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
class DeactivateSchoolServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private Clock clock;

    private DeactivateSchoolUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final Instant DEACTIVATE_NOW = Instant.parse("2025-08-01T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-abc");

    @BeforeEach
    void setUp() {
        useCase = new DeactivateSchoolService(schoolRepository, branchRepository, clock);
    }

    @Test
    void execute_shouldDeactivateActiveSchool() {
        School school = buildSchool(SCHOOL_ID, "Colegio Andino", "CA-001", School.Status.ACTIVE);
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsActiveBySchoolId(SCHOOL_ID)).thenReturn(false);
        when(clock.instant()).thenReturn(DEACTIVATE_NOW);

        SchoolResult result = useCase.execute(SCHOOL_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        assertThat(result.updatedAt()).isEqualTo(DEACTIVATE_NOW);
        verify(schoolRepository).save(any(School.class));
    }

    @Test
    void execute_shouldBeIdempotentForAlreadyInactiveSchool() {
        School school = buildSchool(SCHOOL_ID, "Colegio Cerrado", "CC-001", School.Status.INACTIVE);
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsActiveBySchoolId(SCHOOL_ID)).thenReturn(false);
        when(clock.instant()).thenReturn(DEACTIVATE_NOW);

        SchoolResult result = useCase.execute(SCHOOL_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        // For already inactive school, deactivate() returns same instance
        // We still save it (idempotent)
        verify(schoolRepository).save(any(School.class));
    }

    @Test
    void execute_shouldThrowWhenSchoolNotFound() {
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("School not found");
    }

    @Test
    void execute_shouldThrowWhenSchoolHasActiveBranches() {
        School school = buildSchool(SCHOOL_ID, "Colegio Activo", "CA-001", School.Status.ACTIVE);
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsActiveBySchoolId(SCHOOL_ID)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Cannot deactivate school with active branches");

        verify(schoolRepository, never()).save(any());
    }

    private School buildSchool(SchoolId id, String name, String code, School.Status status) {
        return School.restore(
                id,
                new SchoolName(name),
                new SchoolCode(code),
                SchoolShortName.of(name.substring(0, Math.min(name.length(), 10))),
                SchoolDescription.of("Description"),
                SchoolEmail.of(code.toLowerCase().replace("-", "") + "@school.edu"),
                SchoolPhone.of("+571234567"),
                SchoolAddress.of("Calle 123"),
                status,
                FIXED_NOW,
                FIXED_NOW
        );
    }
}
