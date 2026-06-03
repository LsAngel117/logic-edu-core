package com.logossystemsit.logiceducore.application.academic.subject.usecase;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.ListSubjectsBySchoolUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListSubjectsBySchoolService")
class ListSubjectsBySchoolServiceTest {

    @Mock
    private SubjectRepository repository;

    private ListSubjectsBySchoolUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");

    @BeforeEach
    void setUp() {
        useCase = new ListSubjectsBySchoolService(repository);
    }

    @Test
    @DisplayName("should return subjects list for school")
    void shouldReturnSubjectsListForSchool() {
        Subject math = Subject.create(
                SubjectId.generate(), SCHOOL_ID, "MAT101", "Mathematics", null, 120, FIXED_NOW
        );
        Subject physics = Subject.create(
                SubjectId.generate(), SCHOOL_ID, "PHY201", "Physics", "Phys desc", 80, FIXED_NOW
        );
        when(repository.findBySchoolId(SCHOOL_ID)).thenReturn(List.of(math, physics));

        List<SubjectResult> results = useCase.execute(SCHOOL_ID);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).code()).isEqualTo("MAT101");
        assertThat(results.get(1).code()).isEqualTo("PHY201");
        assertThat(results.get(1).description()).isEqualTo("Phys desc");
    }

    @Test
    @DisplayName("should return empty list when school has no subjects")
    void shouldReturnEmptyListWhenSchoolHasNoSubjects() {
        when(repository.findBySchoolId(SCHOOL_ID)).thenReturn(List.of());

        List<SubjectResult> results = useCase.execute(SCHOOL_ID);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should return subjects for different school")
    void shouldReturnSubjectsForDifferentSchool() {
        SchoolId otherSchool = new SchoolId("880e8400-e29b-41d4-a716-446655440003");
        Subject subject = Subject.create(
                SubjectId.generate(), otherSchool, "HIS301", "History", null, 90, FIXED_NOW
        );
        when(repository.findBySchoolId(otherSchool)).thenReturn(List.of(subject));

        List<SubjectResult> results = useCase.execute(otherSchool);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).schoolId()).isEqualTo(otherSchool.value());
    }
}
