package com.logossystemsit.logiceducore.application.school.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.school.dto.command.UpdateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.UpdateSchoolUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
import com.logossystemsit.logiceducore.shared.valueobject.City;
import com.logossystemsit.logiceducore.shared.valueobject.Country;
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
class UpdateSchoolServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private Clock clock;

    private UpdateSchoolUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final Instant UPDATE_NOW = Instant.parse("2025-07-01T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-123");

    @BeforeEach
    void setUp() {
        useCase = new UpdateSchoolService(schoolRepository, clock);
    }

    @Test
    void execute_shouldUpdateAndPersistSchool() {
        School school = buildSchool(SCHOOL_ID, "Colegio Andino", "CA-001", School.Status.ACTIVE);
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(clock.instant()).thenReturn(UPDATE_NOW);

        SchoolName newName = new SchoolName("Colegio Andino Nueva");
        SchoolCode newCode = new SchoolCode("CA-002");
        SchoolShortName newShortName = SchoolShortName.of("Andino Nva");
        SchoolDescription newDesc = SchoolDescription.of("Actualizado");
        SchoolEmail newEmail = SchoolEmail.of("nuevo@andino.edu");
        SchoolPhone newPhone = SchoolPhone.of("+579876543");
        SchoolAddress newAddress = SchoolAddress.of("Carrera 45 #67-89");

        UpdateSchoolCommand command = new UpdateSchoolCommand(
                SCHOOL_ID, newName, newCode, newShortName, newDesc, newEmail, newPhone, newAddress,
                new City("Medellín"), new Country("Colombia")
        );

        SchoolResult result = useCase.execute(command);

        assertThat(result.name()).isEqualTo("Colegio Andino Nueva");
        assertThat(result.code()).isEqualTo("CA-002");
        assertThat(result.shortName()).isEqualTo("Andino Nva");
        assertThat(result.updatedAt()).isEqualTo(UPDATE_NOW);
        verify(schoolRepository).save(any(School.class));
    }

    @Test
    void execute_shouldRejectInactiveSchool() {
        School school = buildSchool(SCHOOL_ID, "Colegio Cerrado", "CC-001", School.Status.INACTIVE);
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));

        UpdateSchoolCommand command = new UpdateSchoolCommand(
                SCHOOL_ID,
                new SchoolName("New Name"),
                new SchoolCode("NN-001"),
                SchoolShortName.of("NN"),
                SchoolDescription.of("Desc"),
                SchoolEmail.of("new@school.edu"),
                SchoolPhone.of("+571234567"),
                SchoolAddress.of("Some address"),
                new City("Medellín"),
                new Country("Colombia")
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot modify an inactive school");

        verify(schoolRepository, never()).save(any());
    }

    @Test
    void execute_shouldThrowWhenSchoolNotFound() {
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.empty());

        UpdateSchoolCommand command = new UpdateSchoolCommand(
                SCHOOL_ID,
                new SchoolName("New Name"),
                new SchoolCode("NN-001"),
                SchoolShortName.of("NN"),
                SchoolDescription.of("Desc"),
                SchoolEmail.of("new@school.edu"),
                SchoolPhone.of("+571234567"),
                SchoolAddress.of("Some address"),
                new City("Medellín"),
                new Country("Colombia")
        );

        assertThatThrownBy(() -> useCase.execute(command))
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
                new City("Medellín"),
                new Country("Colombia"),
                status,
                FIXED_NOW,
                FIXED_NOW
        );
    }
}
