package com.logossystemsit.logiceducore.application.academic.subject.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.academic.subject.dto.command.UpdateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.UpdateSubjectUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectStatus;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("UpdateSubjectService")
class UpdateSubjectServiceTest {

    @Mock
    private SubjectRepository repository;

    @Mock
    private Clock clock;

    private UpdateSubjectUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final SubjectId SUBJECT_ID = new SubjectId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new UpdateSubjectService(repository, clock);
    }

    @Test
    @DisplayName("should update all fields when no conflict")
    void shouldUpdateAllFieldsWhenNoConflict() {
        Subject existing = Subject.create(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", "Basic math", 120, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(existing));
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));
        when(repository.existsBySchoolIdAndCode(SCHOOL_ID, "MAT102")).thenReturn(false);

        UpdateSubjectCommand command = new UpdateSubjectCommand(
                SUBJECT_ID, SCHOOL_ID, "MAT102", "Advanced Math", "Advanced course", 150
        );

        SubjectResult result = useCase.execute(command);

        assertThat(result.code()).isEqualTo("MAT102");
        assertThat(result.name()).isEqualTo("Advanced Math");
        assertThat(result.description()).isEqualTo("Advanced course");
        assertThat(result.hours()).isEqualTo(150);
        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("should throw when subject not found")
    void shouldThrowWhenSubjectNotFound() {
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        UpdateSubjectCommand command = new UpdateSubjectCommand(
                SUBJECT_ID, SCHOOL_ID, "X", "Name", null, 120
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Subject not found");
    }

    @Test
    @DisplayName("should throw when subject is inactive")
    void shouldThrowWhenSubjectIsInactive() {
        Subject inactive = Subject.restore(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120,
                SubjectStatus.INACTIVE, FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(inactive));

        UpdateSubjectCommand command = new UpdateSubjectCommand(
                SUBJECT_ID, SCHOOL_ID, "X", "Name", null, 120
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("should throw when new code conflicts with another subject in same school")
    void shouldThrowWhenNewCodeConflicts() {
        Subject existing = Subject.create(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(existing));
        when(repository.existsBySchoolIdAndCode(SCHOOL_ID, "MAT102")).thenReturn(true);

        UpdateSubjectCommand command = new UpdateSubjectCommand(
                SUBJECT_ID, SCHOOL_ID, "MAT102", "Math Updated", null, 130
        );

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("should not check uniqueness when code unchanged")
    void shouldNotCheckUniquenessWhenCodeUnchanged() {
        Subject existing = Subject.create(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(existing));
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

        UpdateSubjectCommand command = new UpdateSubjectCommand(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics Updated", "New desc", 120
        );

        SubjectResult result = useCase.execute(command);

        assertThat(result.name()).isEqualTo("Mathematics Updated");
        assertThat(result.code()).isEqualTo("MAT101");
        verify(repository, never()).existsBySchoolIdAndCode(any(), any());
    }
}
