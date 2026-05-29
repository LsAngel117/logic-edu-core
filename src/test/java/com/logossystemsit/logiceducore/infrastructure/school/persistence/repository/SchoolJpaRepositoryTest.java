package com.logossystemsit.logiceducore.infrastructure.school.persistence.repository;

import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.entity.SchoolEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.flyway.enabled=false"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class SchoolJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SchoolJpaRepository repository;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createQuery("DELETE FROM SchoolEntity").executeUpdate();
    }

    @Test
    void save_shouldPersistSchool() {
        SchoolEntity entity = buildEntity("school-1", "Colegio Andino", "CA-001", School.Status.ACTIVE);

        SchoolEntity saved = repository.save(entity);

        assertThat(saved.getId()).isEqualTo("school-1");
        assertThat(saved.getName()).isEqualTo("Colegio Andino");
        assertThat(saved.getCode()).isEqualTo("CA-001");
        assertThat(saved.getStatus()).isEqualTo(School.Status.ACTIVE);
    }

    @Test
    void findById_shouldReturnSchoolWhenFound() {
        SchoolEntity entity = buildEntity("school-2", "Liceo del Sur", "LS-002", School.Status.ACTIVE);
        entityManager.persist(entity);
        entityManager.flush();

        Optional<SchoolEntity> result = repository.findById("school-2");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Liceo del Sur");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        Optional<SchoolEntity> result = repository.findById("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByName_shouldReturnSchoolWhenFound() {
        SchoolEntity entity = buildEntity("school-3", "Instituto Técnico", "IT-003", School.Status.ACTIVE);
        entityManager.persist(entity);
        entityManager.flush();

        Optional<SchoolEntity> result = repository.findByName("Instituto Técnico");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("school-3");
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotFound() {
        Optional<SchoolEntity> result = repository.findByName("Nonexistent School");

        assertThat(result).isEmpty();
    }

    @Test
    void findByCode_shouldReturnSchoolWhenFound() {
        SchoolEntity entity = buildEntity("school-4", "Escuela Nueva", "EN-004", School.Status.ACTIVE);
        entityManager.persist(entity);
        entityManager.flush();

        Optional<SchoolEntity> result = repository.findByCode("EN-004");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Escuela Nueva");
    }

    @Test
    void findByCode_shouldReturnEmptyWhenNotFound() {
        Optional<SchoolEntity> result = repository.findByCode("XX-999");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByName_shouldReturnTrueWhenExists() {
        SchoolEntity entity = buildEntity("school-5", "Colegio Mayor", "CM-005", School.Status.ACTIVE);
        entityManager.persist(entity);
        entityManager.flush();

        assertThat(repository.existsByName("Colegio Mayor")).isTrue();
        assertThat(repository.existsByName("Nonexistent")).isFalse();
    }

    @Test
    void existsByCode_shouldReturnTrueWhenExists() {
        SchoolEntity entity = buildEntity("school-6", "Instituto Central", "IC-006", School.Status.ACTIVE);
        entityManager.persist(entity);
        entityManager.flush();

        assertThat(repository.existsByCode("IC-006")).isTrue();
        assertThat(repository.existsByCode("XX-000")).isFalse();
    }

    @Test
    void uniqueConstraintOnName_shouldRejectDuplicate() {
        SchoolEntity e1 = buildEntity("school-7a", "Colegio Único", "CU-A", School.Status.ACTIVE);
        repository.saveAndFlush(e1);

        SchoolEntity e2 = buildEntity("school-7b", "Colegio Único", "CU-B", School.Status.ACTIVE);

        assertThatThrownBy(() -> repository.saveAndFlush(e2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void uniqueConstraintOnCode_shouldRejectDuplicate() {
        SchoolEntity e1 = buildEntity("school-8a", "Alpha School", "CODE-UNIQUE", School.Status.ACTIVE);
        repository.saveAndFlush(e1);

        SchoolEntity e2 = buildEntity("school-8b", "Beta School", "CODE-UNIQUE", School.Status.ACTIVE);

        assertThatThrownBy(() -> repository.saveAndFlush(e2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldPersistInactiveSchool() {
        SchoolEntity entity = buildEntity("school-9", "Escuela Cerrada", "EC-009", School.Status.INACTIVE);

        SchoolEntity saved = repository.save(entity);

        assertThat(saved.getStatus()).isEqualTo(School.Status.INACTIVE);
    }

    private SchoolEntity buildEntity(String id, String name, String code, School.Status status) {
        SchoolEntity e = new SchoolEntity();
        e.setId(id);
        e.setName(name);
        e.setCode(code);
        e.setShortName(name.substring(0, Math.min(name.length(), 10)));
        e.setDescription("Description of " + name);
        e.setEmail("contacto@" + code.toLowerCase().replace("-", "") + ".edu");
        e.setPhone("+571234567");
        e.setAddress("Calle 123 #45-67");
        e.setStatus(status);
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
