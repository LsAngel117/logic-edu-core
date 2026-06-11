package com.logossystemsit.logiceducore.infrastructure.school.persistence.adapter;

import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
import com.logossystemsit.logiceducore.shared.valueobject.City;
import com.logossystemsit.logiceducore.shared.valueobject.Country;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.entity.SchoolEntity;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.repository.SchoolJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolRepositoryAdapterTest {

    @Mock
    private SchoolJpaRepository jpa;

    private SchoolRepository adapter;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        adapter = new SchoolRepositoryAdapter(jpa);
    }

    @Test
    void save_shouldMapToEntityAndDelegateToJpaRepository() {
        School school = buildSchool("school-1", "Colegio Andino", "CA-001", School.Status.ACTIVE);

        adapter.save(school);

        ArgumentCaptor<SchoolEntity> captor = ArgumentCaptor.forClass(SchoolEntity.class);
        verify(jpa).save(captor.capture());
        SchoolEntity entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo("school-1");
        assertThat(entity.getName()).isEqualTo("Colegio Andino");
        assertThat(entity.getCode()).isEqualTo("CA-001");
        assertThat(entity.getShortName()).isEqualTo(school.getShortName().value());
        assertThat(entity.getStatus()).isEqualTo(School.Status.ACTIVE);
    }

    @Test
    void findById_shouldReturnDomainObjectWhenFound() {
        SchoolEntity entity = buildEntity("school-2", "Liceo del Sur", "LS-002", School.Status.ACTIVE);
        when(jpa.findById("school-2")).thenReturn(Optional.of(entity));

        Optional<School> result = adapter.findById(new SchoolId("school-2"));

        assertThat(result).isPresent();
        assertThat(result.get().getId().value()).isEqualTo("school-2");
        assertThat(result.get().getName().value()).isEqualTo("Liceo del Sur");
        verify(jpa).findById("school-2");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(jpa.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<School> result = adapter.findById(new SchoolId("nonexistent"));

        assertThat(result).isEmpty();
        verify(jpa).findById("nonexistent");
    }

    @Test
    void findAll_shouldReturnListOfDomainObjects() {
        SchoolEntity e1 = buildEntity("school-3", "Instituto A", "IA-003", School.Status.ACTIVE);
        SchoolEntity e2 = buildEntity("school-4", "Instituto B", "IB-004", School.Status.ACTIVE);
        when(jpa.findAll()).thenReturn(List.of(e1, e2));

        List<School> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName().value()).isEqualTo("Instituto A");
        assertThat(result.get(1).getName().value()).isEqualTo("Instituto B");
        verify(jpa).findAll();
    }

    @Test
    void existsByName_shouldDelegateToJpaRepository() {
        when(jpa.existsByName("Colegio Test")).thenReturn(true);

        boolean result = adapter.existsByName(new SchoolName("Colegio Test"));

        assertThat(result).isTrue();
        verify(jpa).existsByName("Colegio Test");
    }

    @Test
    void existsByName_shouldReturnFalseWhenNotExists() {
        when(jpa.existsByName("Nonexistent")).thenReturn(false);

        boolean result = adapter.existsByName(new SchoolName("Nonexistent"));

        assertThat(result).isFalse();
        verify(jpa).existsByName("Nonexistent");
    }

    @Test
    void existsByCode_shouldDelegateToJpaRepository() {
        when(jpa.existsByCode("CD-001")).thenReturn(true);

        boolean result = adapter.existsByCode(new SchoolCode("CD-001"));

        assertThat(result).isTrue();
        verify(jpa).existsByCode("CD-001");
    }

    @Test
    void existsByCode_shouldReturnFalseWhenNotExists() {
        when(jpa.existsByCode("XX-000")).thenReturn(false);

        boolean result = adapter.existsByCode(new SchoolCode("XX-000"));

        assertThat(result).isFalse();
        verify(jpa).existsByCode("XX-000");
    }

    @Test
    void findByName_shouldReturnDomainObjectWhenFound() {
        SchoolEntity entity = buildEntity("school-5", "Colegio Alpha", "CA-005", School.Status.ACTIVE);
        when(jpa.findByName("Colegio Alpha")).thenReturn(Optional.of(entity));

        Optional<School> result = adapter.findByName(new SchoolName("Colegio Alpha"));

        assertThat(result).isPresent();
        assertThat(result.get().getCode().value()).isEqualTo("CA-005");
        verify(jpa).findByName("Colegio Alpha");
    }

    @Test
    void findByCode_shouldReturnDomainObjectWhenFound() {
        SchoolEntity entity = buildEntity("school-6", "Colegio Beta", "CB-006", School.Status.ACTIVE);
        when(jpa.findByCode("CB-006")).thenReturn(Optional.of(entity));

        Optional<School> result = adapter.findByCode(new SchoolCode("CB-006"));

        assertThat(result).isPresent();
        assertThat(result.get().getName().value()).isEqualTo("Colegio Beta");
        verify(jpa).findByCode("CB-006");
    }

    @Test
    void save_shouldHandleOptionalFields() {
        // School with null description, email, phone, address
        SchoolEntity entityWithNulls = buildEntity("school-7", "Colegio Minimal", "CM-007", School.Status.ACTIVE);
        // Override optional fields to null
        entityWithNulls.setDescription(null);
        entityWithNulls.setEmail(null);
        entityWithNulls.setPhone(null);
        entityWithNulls.setAddress(null);

        when(jpa.findById("school-7")).thenReturn(Optional.of(entityWithNulls));

        Optional<School> result = adapter.findById(new SchoolId("school-7"));

        assertThat(result).isPresent();
        assertThat(result.get().getDescription().isEmpty()).isTrue();
        assertThat(result.get().getAddress().isEmpty()).isTrue();
    }

    private School buildSchool(String id, String name, String code, School.Status status) {
        return School.restore(
                new SchoolId(id),
                new SchoolName(name),
                new SchoolCode(code),
                SchoolShortName.of(name.substring(0, Math.min(name.length(), 10))),
                SchoolDescription.of("Description of " + name),
                SchoolEmail.of(code.toLowerCase().replace("-", "") + "@school.edu"),
                SchoolPhone.of("+571234567"),
                SchoolAddress.of("Calle 123 #45-67"),
                new City("Medellín"),
                new Country("Colombia"),
                status,
                NOW,
                NOW
        );
    }

    private SchoolEntity buildEntity(String id, String name, String code, School.Status status) {
        SchoolEntity e = new SchoolEntity();
        e.setId(id);
        e.setName(name);
        e.setCode(code);
        e.setShortName(name.substring(0, Math.min(name.length(), 10)));
        e.setDescription("Description of " + name);
        e.setEmail(code.toLowerCase().replace("-", "") + "@school.edu");
        e.setPhone("+571234567");
        e.setAddress("Calle 123 #45-67");
        e.setCity("Medellín");
        e.setCountry("Colombia");
        e.setStatus(status);
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
