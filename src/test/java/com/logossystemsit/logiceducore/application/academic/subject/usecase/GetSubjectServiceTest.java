package com.logossystemsit.logiceducore.application.academic.subject.usecase;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.GetSubjectUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
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
@DisplayName("GetSubjectService")
class GetSubjectServiceTest {

    @Mock
    private SubjectRepository repository;

    private GetSubjectUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final SubjectId SUBJECT_ID = new SubjectId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        useCase = new GetSubjectService(repository);
    }

    @Test
    @DisplayName("should return subject result when found")
    void shouldReturnSubjectResultWhenFound() {
        Subject subject = Subject.create(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", "Basic math", 120, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));

        SubjectResult result = useCase.execute(SUBJECT_ID);

        assertThat(result.id()).isEqualTo(SUBJECT_ID.value());
        assertThat(result.code()).isEqualTo("MAT101");
        assertThat(result.name()).isEqualTo("Mathematics");
        assertThat(result.description()).isEqualTo("Basic math");
        assertThat(result.hours()).isEqualTo(120);
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("should throw when subject not found")
    void shouldThrowWhenSubjectNotFound() {
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(SUBJECT_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Subject not found");
    }

    @Test
    @DisplayName("should return inactive subject")
    void shouldReturnInactiveSubject() {
        Subject inactive = Subject.restore(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", null, 120,
                com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectStatus.INACTIVE,
                FIXED_NOW, FIXED_NOW
        );
        when(repository.findById(SUBJECT_ID)).thenReturn(Optional.of(inactive));

        SubjectResult result = useCase.execute(SUBJECT_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        assertThat(result.code()).isEqualTo("MAT101");
    }
}
