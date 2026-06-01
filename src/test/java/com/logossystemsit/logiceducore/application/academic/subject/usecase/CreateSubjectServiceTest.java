package com.logossystemsit.logiceducore.application.academic.subject.usecase;

import com.logossystemsit.logiceducore.application.academic.subject.dto.command.CreateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.CreateSubjectUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.school.model.School;
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
@DisplayName("CreateSubjectService")
class CreateSubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private Clock clock;

    private CreateSubjectUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final SubjectId SUBJECT_ID = new SubjectId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new CreateSubjectService(subjectRepository, schoolRepository, clock);
    }

    private static School mockSchool() {
        return mock(School.class);
    }

    @Test
    @DisplayName("should create subject when school exists and code is unique")
    void shouldCreateSubjectWhenSchoolExistsAndCodeIsUnique() {
        School school = mockSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(clock.instant()).thenReturn(FIXED_NOW);
        when(subjectRepository.existsBySchoolIdAndCode(SCHOOL_ID, "MAT101")).thenReturn(false);

        CreateSubjectCommand command = new CreateSubjectCommand(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", "Basic math", 120
        );

        SubjectResult result = useCase.execute(command);

        assertThat(result.id()).isEqualTo(SUBJECT_ID.value());
        assertThat(result.schoolId()).isEqualTo(SCHOOL_ID.value());
        assertThat(result.code()).isEqualTo("MAT101");
        assertThat(result.name()).isEqualTo("Mathematics");
        assertThat(result.description()).isEqualTo("Basic math");
        assertThat(result.hours()).isEqualTo(120);
        assertThat(result.status()).isEqualTo("ACTIVE");

        ArgumentCaptor<Subject> captor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(captor.capture());
        Subject saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(SUBJECT_ID);
        assertThat(saved.getCode()).isEqualTo("MAT101");
        assertThat(saved.getStatus().name()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("should throw when school does not exist")
    void shouldThrowWhenSchoolDoesNotExist() {
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.empty());

        CreateSubjectCommand command = new CreateSubjectCommand(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("School not found");

        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw when code already exists for school")
    void shouldThrowWhenCodeAlreadyExistsForSchool() {
        School school = mockSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(subjectRepository.existsBySchoolIdAndCode(SCHOOL_ID, "MAT101")).thenReturn(true);

        CreateSubjectCommand command = new CreateSubjectCommand(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("should create subject with different data")
    void shouldCreateSubjectWithDifferentData() {
        SchoolId otherSchool = new SchoolId("880e8400-e29b-41d4-a716-446655440003");
        School school = mockSchool();
        when(schoolRepository.findById(otherSchool)).thenReturn(Optional.of(school));
        when(clock.instant()).thenReturn(FIXED_NOW);
        when(subjectRepository.existsBySchoolIdAndCode(otherSchool, "PHY201")).thenReturn(false);

        CreateSubjectCommand command = new CreateSubjectCommand(
                SUBJECT_ID, otherSchool, "PHY201", "Physics", null, 80
        );

        SubjectResult result = useCase.execute(command);

        assertThat(result.code()).isEqualTo("PHY201");
        assertThat(result.name()).isEqualTo("Physics");
        assertThat(result.hours()).isEqualTo(80);
        verify(subjectRepository).save(any());
    }
}
