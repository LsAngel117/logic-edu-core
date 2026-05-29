package com.logossystemsit.logiceducore.application.school.usecase;

import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.ListSchoolsUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListSchoolsServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    private ListSchoolsUseCase useCase;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        useCase = new ListSchoolsService(schoolRepository);
    }

    @Test
    void execute_shouldReturnAllSchools() {
        School school1 = buildSchool("school-1", "Colegio A", "CA-001", School.Status.ACTIVE);
        School school2 = buildSchool("school-2", "Colegio B", "CB-002", School.Status.ACTIVE);

        when(schoolRepository.findAll()).thenReturn(List.of(school1, school2));

        List<SchoolResult> results = useCase.execute();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).name()).isEqualTo("Colegio A");
        assertThat(results.get(1).name()).isEqualTo("Colegio B");
        verify(schoolRepository).findAll();
    }

    @Test
    void execute_shouldReturnEmptyListWhenNoSchools() {
        when(schoolRepository.findAll()).thenReturn(List.of());

        List<SchoolResult> results = useCase.execute();

        assertThat(results).isEmpty();
        verify(schoolRepository).findAll();
    }

    private School buildSchool(String id, String name, String code, School.Status status) {
        return School.restore(
                new SchoolId(id),
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
