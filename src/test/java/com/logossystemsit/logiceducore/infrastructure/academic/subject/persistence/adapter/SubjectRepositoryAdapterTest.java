package com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.adapter;

import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectStatus;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.entity.SubjectEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.repository.SubjectJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectRepositoryAdapter")
class SubjectRepositoryAdapterTest {

    @Mock
    private SubjectJpaRepository jpa;

    private SubjectRepositoryAdapter adapter;

    private static final Instant FIXED_NOW = Instant.parse("2026-01-15T10:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("550e8400-e29b-41d4-a716-446655440000");
    private static final SubjectId SUBJECT_ID = new SubjectId("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        adapter = new SubjectRepositoryAdapter(jpa);
    }

    @Test
    @DisplayName("should map domain to entity and save")
    void shouldMapDomainToEntityAndSave() {
        Subject subject = Subject.create(
                SUBJECT_ID, SCHOOL_ID, "MAT101", "Mathematics", "Basic math", 120, FIXED_NOW
        );

        adapter.save(subject);

        ArgumentCaptor<SubjectEntity> captor = ArgumentCaptor.forClass(SubjectEntity.class);
        verify(jpa).save(captor.capture());
        SubjectEntity entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo(SUBJECT_ID.value());
        assertThat(entity.getSchoolId()).isEqualTo(SCHOOL_ID.value());
        assertThat(entity.getCode()).isEqualTo("MAT101");
        assertThat(entity.getName()).isEqualTo("Mathematics");
        assertThat(entity.getDescription()).isEqualTo("Basic math");
        assertThat(entity.getHours()).isEqualTo(120);
        assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        assertThat(entity.getCreatedAt()).isEqualTo(FIXED_NOW);
        assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_NOW);
    }

    @Test
    @DisplayName("should map entity to domain when found")
    void shouldMapEntityToDomainWhenFound() {
        SubjectEntity entity = new SubjectEntity();
        entity.setId(SUBJECT_ID.value());
        entity.setSchoolId(SCHOOL_ID.value());
        entity.setCode("PHY201");
        entity.setName("Physics");
        entity.setDescription(null);
        entity.setHours(80);
        entity.setStatus("INACTIVE");
        entity.setCreatedAt(FIXED_NOW);
        entity.setUpdatedAt(FIXED_NOW.plusSeconds(3600));
        when(jpa.findById(SUBJECT_ID.value())).thenReturn(Optional.of(entity));

        Optional<Subject> result = adapter.findById(SUBJECT_ID);

        assertThat(result).isPresent();
        Subject subject = result.get();
        assertThat(subject.getId()).isEqualTo(SUBJECT_ID);
        assertThat(subject.getSchoolId()).isEqualTo(SCHOOL_ID);
        assertThat(subject.getCode()).isEqualTo("PHY201");
        assertThat(subject.getName()).isEqualTo("Physics");
        assertThat(subject.getDescription()).isNull();
        assertThat(subject.getHours()).isEqualTo(80);
        assertThat(subject.getStatus()).isEqualTo(SubjectStatus.INACTIVE);
        assertThat(subject.getUpdatedAt()).isEqualTo(FIXED_NOW.plusSeconds(3600));
    }

    @Test
    @DisplayName("should return empty optional when not found")
    void shouldReturnEmptyOptionalWhenNotFound() {
        when(jpa.findById(SUBJECT_ID.value())).thenReturn(Optional.empty());

        Optional<Subject> result = adapter.findById(SUBJECT_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should map domain with null description to entity")
    void shouldMapDomainWithNullDescriptionToEntity() {
        Subject subject = Subject.create(
                SUBJECT_ID, SCHOOL_ID, "LAB01", "Laboratory", null, 0, FIXED_NOW
        );

        adapter.save(subject);

        ArgumentCaptor<SubjectEntity> captor = ArgumentCaptor.forClass(SubjectEntity.class);
        verify(jpa).save(captor.capture());
        SubjectEntity entity = captor.getValue();
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getHours()).isEqualTo(0);
    }
}
