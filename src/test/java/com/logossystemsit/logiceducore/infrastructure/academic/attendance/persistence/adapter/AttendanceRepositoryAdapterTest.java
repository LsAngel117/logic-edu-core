package com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.Attendance;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject.AttendanceId;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.valueobject.AttendanceStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.entity.AttendanceEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.repository.AttendanceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceRepositoryAdapter")
class AttendanceRepositoryAdapterTest {

    @Mock
    private AttendanceJpaRepository jpa;

    private AttendanceRepository adapter;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final AttendanceId ATTENDANCE_ID = AttendanceId.generate();
    private static final GroupId GROUP_ID = GroupId.generate();
    private static final UserId STUDENT_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final LocalDate ATTENDANCE_DATE = LocalDate.of(2026, 6, 1);

    @BeforeEach
    void setUp() {
        adapter = new AttendanceRepositoryAdapter(jpa);
    }

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("should map attendance to entity and save")
        void shouldMapAndSave() {
            Attendance attendance = Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.PRESENT, "On time", FIXED_NOW
            );

            adapter.save(attendance);

            ArgumentCaptor<AttendanceEntity> captor = ArgumentCaptor.forClass(AttendanceEntity.class);
            verify(jpa).save(captor.capture());
            AttendanceEntity entity = captor.getValue();

            assertThat(entity.getId()).isEqualTo(ATTENDANCE_ID.value());
            assertThat(entity.getGroupId()).isEqualTo(GROUP_ID.value());
            assertThat(entity.getStudentId()).isEqualTo(STUDENT_ID.value());
            assertThat(entity.getDate()).isEqualTo(ATTENDANCE_DATE);
            assertThat(entity.getStatus()).isEqualTo("PRESENT");
            assertThat(entity.getObservations()).isEqualTo("On time");
            assertThat(entity.getCreatedAt()).isEqualTo(FIXED_NOW);
            assertThat(entity.getUpdatedAt()).isEqualTo(FIXED_NOW);
        }

        @Test
        @DisplayName("should save attendance with null observations")
        void shouldSaveWithNullObservations() {
            Attendance attendance = Attendance.create(
                    ATTENDANCE_ID, GROUP_ID, STUDENT_ID, ATTENDANCE_DATE,
                    AttendanceStatus.ABSENT, null, FIXED_NOW
            );

            adapter.save(attendance);

            ArgumentCaptor<AttendanceEntity> captor = ArgumentCaptor.forClass(AttendanceEntity.class);
            verify(jpa).save(captor.capture());
            assertThat(captor.getValue().getObservations()).isNull();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("should find by ID and map to domain")
        void shouldFindByIdAndMapToDomain() {
            AttendanceEntity entity = createSampleEntity();
            when(jpa.findById(ATTENDANCE_ID.value())).thenReturn(Optional.of(entity));

            Optional<Attendance> result = adapter.findById(ATTENDANCE_ID);

            assertThat(result).isPresent();
            Attendance domain = result.get();
            assertThat(domain.getId()).isEqualTo(ATTENDANCE_ID);
            assertThat(domain.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(jpa.findById("nonexistent")).thenReturn(Optional.empty());

            Optional<Attendance> result = adapter.findById(new AttendanceId("nonexistent"));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByGroupId")
    class FindByGroupIdTests {

        @Test
        @DisplayName("should find by group ID")
        void shouldFindByGroupId() {
            AttendanceEntity entity = createSampleEntity();
            when(jpa.findByGroupId(GROUP_ID.value())).thenReturn(List.of(entity));

            List<Attendance> results = adapter.findByGroupId(GROUP_ID);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getGroupId()).isEqualTo(GROUP_ID);
        }
    }

    @Nested
    @DisplayName("findByGroupIdAndDate")
    class FindByGroupIdAndDateTests {

        @Test
        @DisplayName("should find by group and date")
        void shouldFindByGroupAndDate() {
            AttendanceEntity entity = createSampleEntity();
            when(jpa.findByGroupIdAndDate(GROUP_ID.value(), ATTENDANCE_DATE))
                    .thenReturn(List.of(entity));

            List<Attendance> results = adapter.findByGroupIdAndDate(GROUP_ID, ATTENDANCE_DATE);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getDate()).isEqualTo(ATTENDANCE_DATE);
        }
    }

    @Nested
    @DisplayName("findByGroupIdAndDateAndStudentId")
    class FindByGroupIdAndDateAndStudentIdTests {

        @Test
        @DisplayName("should find by group, date, and student")
        void shouldFindByGroupDateAndStudent() {
            AttendanceEntity entity = createSampleEntity();
            when(jpa.findByGroupIdAndDateAndStudentId(GROUP_ID.value(), ATTENDANCE_DATE, STUDENT_ID.value()))
                    .thenReturn(Optional.of(entity));

            Optional<Attendance> result = adapter.findByGroupIdAndDateAndStudentId(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID);

            assertThat(result).isPresent();
            assertThat(result.get().getStudentId()).isEqualTo(STUDENT_ID);
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenStudentNotFound() {
            when(jpa.findByGroupIdAndDateAndStudentId(any(), any(), any()))
                    .thenReturn(Optional.empty());

            Optional<Attendance> result = adapter.findByGroupIdAndDateAndStudentId(
                    GROUP_ID, ATTENDANCE_DATE, STUDENT_ID);

            assertThat(result).isEmpty();
        }
    }

    // ======================== helpers ========================

    private AttendanceEntity createSampleEntity() {
        AttendanceEntity entity = new AttendanceEntity();
        entity.setId(ATTENDANCE_ID.value());
        entity.setGroupId(GROUP_ID.value());
        entity.setStudentId(STUDENT_ID.value());
        entity.setDate(ATTENDANCE_DATE);
        entity.setStatus("PRESENT");
        entity.setObservations(null);
        entity.setCreatedAt(FIXED_NOW);
        entity.setUpdatedAt(FIXED_NOW);
        return entity;
    }
}
