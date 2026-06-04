package com.logossystemsit.logiceducore.application.school.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import com.logossystemsit.logiceducore.application.school.dto.command.CreateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.CreateSchoolUseCase;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSchoolServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private Clock clock;

    private CreateSchoolUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final SchoolId SCHOOL_ID = SchoolId.generate();
    private static final SchoolName NAME = new SchoolName("Colegio Andino");
    private static final SchoolCode CODE = new SchoolCode("CA-001");
    private static final SchoolShortName SHORT_NAME = SchoolShortName.of("Col.Andino");
    private static final SchoolDescription DESCRIPTION = SchoolDescription.of("Colegio bilingüe");
    private static final SchoolEmail EMAIL = SchoolEmail.of("info@andino.edu");
    private static final SchoolPhone PHONE = SchoolPhone.of("+571234567");
    private static final SchoolAddress ADDRESS = SchoolAddress.of("Calle 123 #45-67");

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(FIXED_NOW);
        useCase = new CreateSchoolService(schoolRepository, clock);
    }

    @Test
    void execute_shouldCreateSchoolAndPersist() {
        CreateSchoolCommand command = new CreateSchoolCommand(
                SCHOOL_ID, NAME, CODE, SHORT_NAME, DESCRIPTION, EMAIL, PHONE, ADDRESS
        );

        when(schoolRepository.existsByName(NAME)).thenReturn(false);
        when(schoolRepository.existsByCode(CODE)).thenReturn(false);

        SchoolResult result = useCase.execute(command);

        assertThat(result.id()).isEqualTo(SCHOOL_ID.value());
        assertThat(result.name()).isEqualTo("Colegio Andino");
        assertThat(result.code()).isEqualTo("CA-001");
        assertThat(result.status()).isEqualTo("ACTIVE");

        verify(schoolRepository).existsByName(NAME);
        verify(schoolRepository).existsByCode(CODE);
        verify(schoolRepository).save(any(School.class));
    }

    @Test
    void execute_shouldSetCorrectTimestamps() {
        CreateSchoolCommand command = new CreateSchoolCommand(
                SCHOOL_ID, NAME, CODE, SHORT_NAME, DESCRIPTION, EMAIL, PHONE, ADDRESS
        );

        when(schoolRepository.existsByName(NAME)).thenReturn(false);
        when(schoolRepository.existsByCode(CODE)).thenReturn(false);

        SchoolResult result = useCase.execute(command);

        assertThat(result.createdAt()).isEqualTo(FIXED_NOW);
        assertThat(result.updatedAt()).isEqualTo(FIXED_NOW);
    }

    @Test
    void execute_shouldRejectDuplicateName() {
        CreateSchoolCommand command = new CreateSchoolCommand(
                SCHOOL_ID, NAME, CODE, SHORT_NAME, DESCRIPTION, EMAIL, PHONE, ADDRESS
        );

        when(schoolRepository.existsByName(NAME)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("School name already exists");

        verify(schoolRepository, never()).save(any());
    }

    @Test
    void execute_shouldRejectDuplicateCode() {
        CreateSchoolCommand command = new CreateSchoolCommand(
                SCHOOL_ID, NAME, CODE, SHORT_NAME, DESCRIPTION, EMAIL, PHONE, ADDRESS
        );

        when(schoolRepository.existsByName(NAME)).thenReturn(false);
        when(schoolRepository.existsByCode(CODE)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("School code already exists");

        verify(schoolRepository, never()).save(any());
    }

    @Test
    void execute_shouldCreateSchoolWithoutOptionalFields() {
        CreateSchoolCommand command = new CreateSchoolCommand(
                SCHOOL_ID, NAME, CODE, SHORT_NAME,
                SchoolDescription.empty(), null, null, SchoolAddress.empty()
        );

        when(schoolRepository.existsByName(NAME)).thenReturn(false);
        when(schoolRepository.existsByCode(CODE)).thenReturn(false);

        SchoolResult result = useCase.execute(command);

        assertThat(result.id()).isEqualTo(SCHOOL_ID.value());
        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.description()).isNull();
        assertThat(result.email()).isNull();
        assertThat(result.phone()).isNull();
        assertThat(result.address()).isNull();
        verify(schoolRepository).save(any(School.class));
    }
}
