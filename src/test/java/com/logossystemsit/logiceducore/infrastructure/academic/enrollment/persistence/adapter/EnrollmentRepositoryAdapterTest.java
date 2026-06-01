package com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.Enrollment;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentId;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.entity.EnrollmentEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.repository.EnrollmentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("EnrollmentRepositoryAdapter")
class EnrollmentRepositoryAdapterTest {

    @Mock
    private EnrollmentJpaRepository jpa;

    private EnrollmentRepository repository;

    private static final Instant NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final EnrollmentId ENR_ID = new EnrollmentId("enr-001");
    private static final UserId USER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final GroupId GROUP_ID = new GroupId("771e8400-e29b-41d4-a716-446655440005");
    private static final SubjectId SUBJECT_ID = new SubjectId("661e8400-e29b-41d4-a716-446655440006");
    private static final AcademicPeriodId PERIOD_ID = new AcademicPeriodId("551e8400-e29b-41d4-a716-446655440007");

    @BeforeEach
    void setUp() {
        repository = new EnrollmentRepositoryAdapter(jpa);
    }

    private Enrollment sampleEnrollment() {
        return Enrollment.create(ENR_ID, USER_ID, GROUP_ID, NOW);
    }

    private static final String ENR_ID_STR = "enr-001";
    private static final String USER_ID_STR = "990e8400-e29b-41d4-a716-446655440004";
    private static final String GROUP_ID_STR = "771e8400-e29b-41d4-a716-446655440005";
    private static final String SUBJECT_ID_STR = "661e8400-e29b-41d4-a716-446655440006";
    private static final String PERIOD_ID_STR = "551e8400-e29b-41d4-a716-446655440007";

    private EnrollmentEntity sampleEntity() {
        EnrollmentEntity entity = new EnrollmentEntity();
        entity.setId(ENR_ID_STR);
        entity.setUserId(USER_ID_STR);
        entity.setGroupId(GROUP_ID_STR);
        entity.setStatus("ACTIVE");
        entity.setEnrolledAt(NOW);
        entity.setUpdatedAt(NOW);
        return entity;
    }

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("should map and save entity")
        void shouldMapAndSaveEntity() {
            Enrollment enrollment = sampleEnrollment();
            repository.save(enrollment);

            ArgumentCaptor<EnrollmentEntity> captor = ArgumentCaptor.forClass(EnrollmentEntity.class);
            verify(jpa).save(captor.capture());

            EnrollmentEntity saved = captor.getValue();
            assertThat(saved.getId()).isEqualTo(ENR_ID_STR);
            assertThat(saved.getUserId()).isEqualTo(USER_ID_STR);
            assertThat(saved.getGroupId()).isEqualTo(GROUP_ID_STR);
            assertThat(saved.getStatus()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("should return enrollment when found")
        void shouldReturnEnrollmentWhenFound() {
            when(jpa.findById(ENR_ID_STR)).thenReturn(Optional.of(sampleEntity()));

            Optional<Enrollment> result = repository.findById(ENR_ID);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(ENR_ID);
            assertThat(result.get().getUserId().value()).isEqualTo(USER_ID_STR);
            assertThat(result.get().getGroupId().value()).isEqualTo(GROUP_ID_STR);
            assertThat(result.get().getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        }

        @Test
        @DisplayName("should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(jpa.findById(ENR_ID_STR)).thenReturn(Optional.empty());

            Optional<Enrollment> result = repository.findById(ENR_ID);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByGroupId")
    class FindByGroupIdTests {

        @Test
        @DisplayName("should return list of enrollments")
        void shouldReturnListOfEnrollments() {
            EnrollmentEntity e1 = sampleEntity();
            EnrollmentEntity e2 = new EnrollmentEntity();
            e2.setId("enr-002");
            e2.setUserId(USER_ID_STR);
            e2.setGroupId(GROUP_ID_STR);
            e2.setStatus("ACTIVE");
            e2.setEnrolledAt(NOW);
            e2.setUpdatedAt(NOW);

            when(jpa.findByGroupId(GROUP_ID_STR)).thenReturn(List.of(e1, e2));

            List<Enrollment> result = repository.findByGroupId(GROUP_ID);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(e -> e.getId().value())
                    .containsExactly("enr-001", "enr-002");
        }

        @Test
        @DisplayName("should return empty list when no enrollments")
        void shouldReturnEmptyListWhenNoEnrollments() {
            when(jpa.findByGroupId(GROUP_ID_STR)).thenReturn(List.of());

            List<Enrollment> result = repository.findByGroupId(GROUP_ID);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countActiveByGroupId")
    class CountActiveByGroupIdTests {

        @Test
        @DisplayName("should count active enrollments")
        void shouldCountActiveEnrollments() {
            when(jpa.countByGroupIdAndStatus(GROUP_ID_STR, "ACTIVE")).thenReturn(5L);

            long count = repository.countActiveByGroupId(GROUP_ID);

            assertThat(count).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("existsByUserIdAndGroupId")
    class ExistsByUserIdAndGroupIdTests {

        @Test
        @DisplayName("should return true when exists")
        void shouldReturnTrueWhenExists() {
            when(jpa.existsByUserIdAndGroupId(USER_ID_STR, GROUP_ID_STR)).thenReturn(true);

            boolean exists = repository.existsByUserIdAndGroupId(USER_ID, GROUP_ID);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("should return false when not exists")
        void shouldReturnFalseWhenNotExists() {
            when(jpa.existsByUserIdAndGroupId(USER_ID_STR, GROUP_ID_STR)).thenReturn(false);

            boolean exists = repository.existsByUserIdAndGroupId(USER_ID, GROUP_ID);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsActiveByStudentAndSubjectAndPeriod")
    class ExistsActiveByStudentAndSubjectAndPeriodTests {

        @Test
        @DisplayName("should delegate to jpa repository")
        void shouldDelegateToJpaRepository() {
            when(jpa.existsActiveByStudentAndSubjectAndPeriod(USER_ID_STR, SUBJECT_ID_STR, PERIOD_ID_STR))
                    .thenReturn(true);

            boolean exists = repository.existsActiveByStudentAndSubjectAndPeriod(
                    USER_ID, SUBJECT_ID, PERIOD_ID);

            assertThat(exists).isTrue();
            verify(jpa).existsActiveByStudentAndSubjectAndPeriod(USER_ID_STR, SUBJECT_ID_STR, PERIOD_ID_STR);
        }
    }
}
