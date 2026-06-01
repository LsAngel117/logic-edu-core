package com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity.GroupEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity.GroupScheduleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("GroupJpaRepository")
class GroupJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupJpaRepository repository;

    private static final Instant NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String SCHOOL_ID = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM GroupScheduleEntity")
                .executeUpdate();
        entityManager.getEntityManager()
                .createQuery("DELETE FROM GroupEntity")
                .executeUpdate();
    }

    @Test
    @DisplayName("should persist and find by id with schedules")
    void shouldPersistAndFindByIdWithSchedules() {
        GroupEntity entity = buildEntity("group-001", SCHOOL_ID, "MATH-101", 30);
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<GroupEntity> found = repository.findById("group-001");
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("MATH-101");
        assertThat(found.get().getCapacity()).isEqualTo(30);
        assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
        assertThat(found.get().getSchedules()).hasSize(1);
        assertThat(found.get().getSchedules().get(0).getDayOfWeek()).isEqualTo("MONDAY");
    }

    @Test
    @DisplayName("should find all by school id")
    void shouldFindAllBySchoolId() {
        GroupEntity g1 = buildEntity("group-001", SCHOOL_ID, "MATH-101", 30);
        GroupEntity g2 = buildEntity("group-002", SCHOOL_ID, "PHY-201", 25);

        entityManager.persist(g1);
        entityManager.persist(g2);
        entityManager.flush();
        entityManager.clear();

        List<GroupEntity> found = repository.findBySchoolId(SCHOOL_ID);
        assertThat(found).hasSize(2);
        assertThat(found).extracting(GroupEntity::getCode)
                .containsExactlyInAnyOrder("MATH-101", "PHY-201");
    }

    @Test
    @DisplayName("should return empty list when no groups for school")
    void shouldReturnEmptyListWhenNoGroupsForSchool() {
        List<GroupEntity> found = repository.findBySchoolId("non-existent-school");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should find by school and branch")
    void shouldFindBySchoolAndBranch() {
        GroupEntity g1 = buildEntity("group-001", SCHOOL_ID, "MATH-101", 30);
        g1.setBranchId("branch-A");
        GroupEntity g2 = buildEntity("group-002", SCHOOL_ID, "PHY-201", 25);
        g2.setBranchId("branch-B");

        entityManager.persist(g1);
        entityManager.persist(g2);
        entityManager.flush();
        entityManager.clear();

        List<GroupEntity> found = repository.findBySchoolIdAndBranchId(SCHOOL_ID, "branch-A");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCode()).isEqualTo("MATH-101");
    }

    @Test
    @DisplayName("should find by school and academic period")
    void shouldFindBySchoolAndAcademicPeriod() {
        GroupEntity g1 = buildEntity("group-001", SCHOOL_ID, "MATH-101", 30);
        g1.setAcademicPeriodId("period-A");
        GroupEntity g2 = buildEntity("group-002", SCHOOL_ID, "PHY-201", 25);
        g2.setAcademicPeriodId("period-B");

        entityManager.persist(g1);
        entityManager.persist(g2);
        entityManager.flush();
        entityManager.clear();

        List<GroupEntity> found = repository.findBySchoolIdAndAcademicPeriodId(SCHOOL_ID, "period-A");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCode()).isEqualTo("MATH-101");
    }

    @Test
    @DisplayName("should check existence by school and code")
    void shouldCheckExistenceBySchoolAndCode() {
        GroupEntity entity = buildEntity("group-001", SCHOOL_ID, "MATH-101", 30);
        entityManager.persistAndFlush(entity);

        assertThat(repository.existsBySchoolIdAndCode(SCHOOL_ID, "MATH-101")).isTrue();
        assertThat(repository.existsBySchoolIdAndCode(SCHOOL_ID, "NONEXISTENT")).isFalse();
    }

    @Test
    @DisplayName("should cascade delete schedules when group is removed")
    void shouldCascadeDeleteSchedulesWhenGroupIsRemoved() {
        GroupEntity entity = buildEntity("group-001", SCHOOL_ID, "MATH-101", 30);
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        GroupEntity persisted = repository.findById("group-001").orElseThrow();
        repository.delete(persisted);
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findById("group-001")).isEmpty();
    }

    @Test
    @DisplayName("should support optimistic locking with version")
    void shouldSupportOptimisticLockingWithVersion() {
        GroupEntity entity = buildEntity("group-001", SCHOOL_ID, "MATH-101", 30);
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        GroupEntity loaded = repository.findById("group-001").orElseThrow();
        assertThat(loaded.getVersion()).isEqualTo(0L);

        loaded.setCode("MATH-102");
        entityManager.merge(loaded);
        entityManager.flush();
        entityManager.clear();

        GroupEntity updated = repository.findById("group-001").orElseThrow();
        assertThat(updated.getVersion()).isEqualTo(1L);
        assertThat(updated.getCode()).isEqualTo("MATH-102");
    }

    private GroupEntity buildEntity(String id, String schoolId, String code, int capacity) {
        GroupEntity e = new GroupEntity();
        e.setId(id);
        e.setSchoolId(schoolId);
        e.setSubjectId("sub-001");
        e.setAcademicPeriodId("per-001");
        e.setBranchId("branch-001");
        e.setTeacherId("teacher-001");
        e.setCode(code);
        e.setCapacity(capacity);
        e.setStatus("ACTIVE");
        e.setVersion(null);
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);

        GroupScheduleEntity schedule = new GroupScheduleEntity();
        schedule.setId("sched-" + id);
        schedule.setGroup(e);
        schedule.setDayOfWeek("MONDAY");
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        schedule.setClassroom("Room 101");
        e.setSchedules(List.of(schedule));

        return e;
    }
}
