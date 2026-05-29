package com.logossystemsit.logiceducore.infrastructure.branch.persistence.repository;

import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchType;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.entity.BranchEntity;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.flyway.enabled=false"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BranchJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BranchJpaRepository repository;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final String SCHOOL_ID = "school-br-1";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createQuery("DELETE FROM BranchEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM SchoolEntity").executeUpdate();
        entityManager.getEntityManager().flush();

        // Insert parent school for FK
        SchoolEntity school = new SchoolEntity();
        school.setId(SCHOOL_ID);
        school.setName("Escuela Sede");
        school.setCode("ES-001");
        school.setShortName("EscSede");
        school.setDescription("Escuela principal");
        school.setEmail("sede@escuela.edu");
        school.setPhone("+571234567");
        school.setAddress("Calle 1");
        school.setStatus(School.Status.ACTIVE);
        school.setCreatedAt(NOW);
        school.setUpdatedAt(NOW);
        entityManager.persist(school);
        entityManager.flush();
    }

    @Test
    void save_shouldPersistBranch() {
        BranchEntity entity = buildEntity("branch-1", "Sede Norte", "SN-001", BranchType.MAIN, SCHOOL_ID);

        BranchEntity saved = repository.save(entity);

        assertThat(saved.getId()).isEqualTo("branch-1");
        assertThat(saved.getName()).isEqualTo("Sede Norte");
        assertThat(saved.getCode()).isEqualTo("SN-001");
        assertThat(saved.getType()).isEqualTo(BranchType.MAIN);
        assertThat(saved.getSchoolId()).isEqualTo(SCHOOL_ID);
    }

    @Test
    void findById_shouldReturnBranchWhenFound() {
        BranchEntity entity = buildEntity("branch-2", "Sede Sur", "SS-002", BranchType.SECONDARY, SCHOOL_ID);
        entityManager.persist(entity);
        entityManager.flush();

        Optional<BranchEntity> result = repository.findById("branch-2");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Sede Sur");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        Optional<BranchEntity> result = repository.findById("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findBySchoolId_shouldReturnBranchesForSchool() {
        BranchEntity e1 = buildEntity("branch-3a", "Sede A", "SA-003", BranchType.MAIN, SCHOOL_ID);
        BranchEntity e2 = buildEntity("branch-3b", "Sede B", "SB-004", BranchType.SECONDARY, SCHOOL_ID);
        entityManager.persist(e1);
        entityManager.persist(e2);
        entityManager.flush();

        List<BranchEntity> results = repository.findBySchoolId(SCHOOL_ID);

        assertThat(results).hasSize(2);
    }

    @Test
    void existsBySchoolIdAndName_shouldReturnTrueWhenMatches() {
        BranchEntity entity = buildEntity("branch-4", "Sede Unica", "SU-005", BranchType.MAIN, SCHOOL_ID);
        entityManager.persist(entity);
        entityManager.flush();

        assertThat(repository.existsBySchoolIdAndName(SCHOOL_ID, "Sede Unica")).isTrue();
        assertThat(repository.existsBySchoolIdAndName(SCHOOL_ID, "Inexistente")).isFalse();
    }

    @Test
    void countBySchoolIdAndType_shouldCountMatchingBranches() {
        BranchEntity main1 = buildEntity("branch-5a", "Principal A", "PA-006", BranchType.MAIN, SCHOOL_ID);
        BranchEntity sec1 = buildEntity("branch-5b", "Secundaria A", "SC-007", BranchType.SECONDARY, SCHOOL_ID);
        entityManager.persist(main1);
        entityManager.persist(sec1);
        entityManager.flush();

        assertThat(repository.countBySchoolIdAndType(SCHOOL_ID, BranchType.MAIN)).isEqualTo(1);
        assertThat(repository.countBySchoolIdAndType(SCHOOL_ID, BranchType.SECONDARY)).isEqualTo(1);
        assertThat(repository.countBySchoolIdAndType(SCHOOL_ID, BranchType.VIRTUAL)).isZero();
    }

    @Test
    void countActiveBySchoolId_shouldCountOnlyActiveBranches() {
        BranchEntity activeBranch = buildEntity("branch-6a", "Activa", "AC-008", BranchType.SECONDARY, SCHOOL_ID);
        BranchEntity inactiveBranch = buildEntity("branch-6b", "Inactiva", "IN-009", BranchType.SECONDARY, SCHOOL_ID);
        inactiveBranch.setStatus(Branch.Status.INACTIVE);
        entityManager.persist(activeBranch);
        entityManager.persist(inactiveBranch);
        entityManager.flush();

        assertThat(repository.countActiveBySchoolId(SCHOOL_ID, Branch.Status.ACTIVE)).isEqualTo(1);
    }

    @Test
    void existsActiveBySchoolId_shouldReturnTrueWhenActiveBranchExists() {
        BranchEntity activeBranch = buildEntity("branch-7", "Sede Activa", "SD-010", BranchType.MAIN, SCHOOL_ID);
        entityManager.persist(activeBranch);
        entityManager.flush();

        assertThat(repository.existsActiveBySchoolId(SCHOOL_ID, Branch.Status.ACTIVE)).isTrue();
    }

    @Test
    void existsActiveBySchoolId_shouldReturnFalseWhenNoActiveBranches() {
        // No branches inserted
        assertThat(repository.existsActiveBySchoolId(SCHOOL_ID, Branch.Status.ACTIVE)).isFalse();
    }

    @Test
    void uniqueConstraintOnSchoolIdAndName_shouldRejectDuplicate() {
        BranchEntity e1 = buildEntity("branch-8a", "Sede Duplicada", "D1-011", BranchType.SECONDARY, SCHOOL_ID);
        repository.saveAndFlush(e1);

        BranchEntity e2 = buildEntity("branch-8b", "Sede Duplicada", "D2-012", BranchType.VIRTUAL, SCHOOL_ID);

        assertThatThrownBy(() -> repository.saveAndFlush(e2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void foreignKeyConstraint_shouldRejectInvalidSchoolId() {
        BranchEntity entity = buildEntity("branch-9", "Sede Huerfana", "SH-013", BranchType.SECONDARY, "invalid-school");

        assertThatThrownBy(() -> repository.saveAndFlush(entity))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldPersistAllBranchTypes() {
        for (BranchType type : BranchType.values()) {
            String id = "branch-type-" + type.name();
            BranchEntity entity = buildEntity(id, "Sede " + type.name(), "ST-" + type.ordinal(), type, SCHOOL_ID);
            entityManager.persist(entity);
        }
        entityManager.flush();

        List<BranchEntity> results = repository.findBySchoolId(SCHOOL_ID);
        assertThat(results).hasSize(BranchType.values().length);
    }

    private BranchEntity buildEntity(String id, String name, String code, BranchType type, String schoolId) {
        BranchEntity e = new BranchEntity();
        e.setId(id);
        e.setSchoolId(schoolId);
        e.setName(name);
        e.setCode(code);
        e.setShortName(name.substring(0, Math.min(name.length(), 10)));
        e.setDescription("Descripcion " + name);
        e.setEmail("sede@" + code.toLowerCase().replace("-", "") + ".edu");
        e.setPhone("+571234567");
        e.setAddress("Direccion " + name);
        e.setType(type);
        e.setStatus(Branch.Status.ACTIVE);
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
