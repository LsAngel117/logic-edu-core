package com.logossystemsit.logiceducore.application.school.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.GetSchoolUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSchoolServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    private GetSchoolUseCase useCase;

    private static final SchoolId SCHOOL_ID = new SchoolId("123e4567-e89b-12d3-a456-426614174000");
    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        useCase = new GetSchoolService(schoolRepository);
    }

    @Test
    void execute_shouldReturnSchoolWhenFound() {
        School school = buildSchool(SCHOOL_ID, "Colegio Andino", "CA-001", School.Status.ACTIVE);
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));

        SchoolResult result = useCase.execute(SCHOOL_ID);

        assertThat(result.id()).isEqualTo(SCHOOL_ID.value());
        assertThat(result.name()).isEqualTo("Colegio Andino");
        assertThat(result.code()).isEqualTo("CA-001");
        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(schoolRepository).findById(SCHOOL_ID);
    }

    @Test
    void execute_shouldThrowWhenSchoolNotFound() {
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("School not found");
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
                NOW,
                NOW
        );
    }
}
