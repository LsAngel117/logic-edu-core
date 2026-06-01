package com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.entity.AttendanceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AttendanceJpaRepository")
class AttendanceJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AttendanceJpaRepository repository;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final String GROUP_ID = "aaa00001-e29b-41d4-a716-446655440001";
    private static final String STUDENT_ID_1 = "990e8400-e29b-41d4-a716-446655440004";
    private static final String STUDENT_ID_2 = "880e8400-e29b-41d4-a716-446655440003";
    private static final LocalDate DATE = LocalDate.of(2026, 6, 1);

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM AttendanceEntity")
                .executeUpdate();
    }

    private AttendanceEntity buildAttendance(String id, String groupId, String studentId,
                                              LocalDate date, String status) {
        AttendanceEntity entity = new AttendanceEntity();
        entity.setId(id);
        entity.setGroupId(groupId);
        entity.setStudentId(studentId);
        entity.setDate(date);
        entity.setStatus(status);
        entity.setCreatedAt(FIXED_NOW);
        entity.setUpdatedAt(FIXED_NOW);
        return entity;
    }

    @Test
    @DisplayName("should save and find by ID")
    void shouldSaveAndFindById() {
        AttendanceEntity entity = buildAttendance("att-001", GROUP_ID, STUDENT_ID_1, DATE, "PRESENT");
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        Optional<AttendanceEntity> found = repository.findById("att-001");
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo("PRESENT");
        assertThat(found.get().getDate()).isEqualTo(DATE);
        assertThat(found.get().getGroupId()).isEqualTo(GROUP_ID);
        assertThat(found.get().getStudentId()).isEqualTo(STUDENT_ID_1);
    }

    @Test
    @DisplayName("should find by group ID")
    void shouldFindByGroupId() {
        entityManager.persist(buildAttendance("att-001", GROUP_ID, STUDENT_ID_1, DATE, "PRESENT"));
        entityManager.persist(buildAttendance("att-002", GROUP_ID, STUDENT_ID_2, DATE, "ABSENT"));
        entityManager.flush();
        entityManager.clear();

        List<AttendanceEntity> results = repository.findByGroupId(GROUP_ID);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(AttendanceEntity::getId)
                .containsExactlyInAnyOrder("att-001", "att-002");
    }

    @Test
    @DisplayName("should return empty list for unknown group")
    void shouldReturnEmptyListForUnknownGroup() {
        List<AttendanceEntity> results = repository.findByGroupId("unknown-group");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("should find by group ID and date")
    void shouldFindByGroupIdAndDate() {
        entityManager.persist(buildAttendance("att-001", GROUP_ID, STUDENT_ID_1, DATE, "PRESENT"));
        entityManager.flush();
        entityManager.clear();

        List<AttendanceEntity> results = repository.findByGroupIdAndDate(GROUP_ID, DATE);
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("should find by group, date, and student")
    void shouldFindByGroupDateAndStudent() {
        entityManager.persist(buildAttendance("att-001", GROUP_ID, STUDENT_ID_1, DATE, "PRESENT"));
        entityManager.flush();
        entityManager.clear();

        Optional<AttendanceEntity> found = repository.findByGroupIdAndDateAndStudentId(
                GROUP_ID, DATE, STUDENT_ID_1);
        assertThat(found).isPresent();
        assertThat(found.get().getStudentId()).isEqualTo(STUDENT_ID_1);
    }

    @Test
    @DisplayName("should return empty when student not in group/date")
    void shouldReturnEmptyForNonMatchingStudent() {
        entityManager.persist(buildAttendance("att-001", GROUP_ID, STUDENT_ID_1, DATE, "PRESENT"));
        entityManager.flush();
        entityManager.clear();

        Optional<AttendanceEntity> found = repository.findByGroupIdAndDateAndStudentId(
                GROUP_ID, DATE, STUDENT_ID_2);
        assertThat(found).isEmpty();
    }
}
