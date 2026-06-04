package com.logossystemsit.logiceducore.application.academic.subject.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.DeactivateSubjectUseCase;
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
@DisplayName("DeactivateSubjectService")
class DeactivateSubjectServiceTest {

    @Mock
    private SubjectRepository repository;

    @Mock
    private Clock clock;

    private DeactivateSubjectUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final SubjectId SUBJECT_ID = new SubjectId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new DeactivateSubjectService(repository, clock);
    }

    @Test
    @DisplayName("should deactivate active subject")
    void shouldDeactivateActiveSubject() {
        Subject subject = Subject.create(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

        SubjectResult result = useCase.execute(SUBJECT_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("should throw when subject not found")
    void shouldThrowWhenSubjectNotFound() {
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(SUBJECT_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Subject not found");
    }

    @Test
    @DisplayName("should return 200 no-op when already inactive (idempotent)")
    void shouldReturnNoOpWhenAlreadyInactive() {
        Subject inactive = Subject.restore(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120,
                SubjectStatus.INACTIVE, FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(inactive));
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(3600));

        SubjectResult result = useCase.execute(SUBJECT_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("should save even when already inactive to update timestamp")
    void shouldSaveWhenAlreadyInactive() {
        Subject inactive = Subject.restore(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120,
                SubjectStatus.INACTIVE, FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(inactive));
        when(clock.instant()).thenReturn(FIXED_NOW.plusSeconds(7200));

        useCase.execute(SUBJECT_ID);

        verify(repository).save(any());
    }
}
