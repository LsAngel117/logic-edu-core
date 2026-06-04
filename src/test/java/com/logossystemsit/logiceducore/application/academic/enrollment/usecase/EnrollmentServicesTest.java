package com.logossystemsit.logiceducore.application.academic.enrollment.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.DropEnrollmentUseCase;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.GetEnrollmentUseCase;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.in.ListEnrollmentsByGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.Enrollment;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.valueobject.EnrollmentId;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.valueobject.EnrollmentStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Enrollment application services")
class EnrollmentServicesTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private Clock clock;

    private static final Instant FIXED_NOW = Instant.parse("2026-06-01T10:00:00Z");
    private static final Instant LATER = Instant.parse("2026-06-02T10:00:00Z");
    private static final EnrollmentId ENR_ID = new EnrollmentId("enr-001");
    private static final UserId USER_ID = new UserId("990e8400-e29b-41d4-a716-446655440004");
    private static final GroupId GROUP_ID = new GroupId("771e8400-e29b-41d4-a716-446655440005");

    private Enrollment sampleEnrollment;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_NOW);
        sampleEnrollment = Enrollment.restore(
                ENR_ID, USER_ID, GROUP_ID,
                EnrollmentStatus.ACTIVE,
                FIXED_NOW, FIXED_NOW
        );
    }

    // ======================== GetEnrollment ========================

    @Nested
    @DisplayName("GetEnrollmentService")
    class GetEnrollmentTests {

        private GetEnrollmentUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new GetEnrollmentService(enrollmentRepository);
        }

        @Test
        @DisplayName("should return enrollment when found")
        void shouldReturnEnrollmentWhenFound() {
            when(enrollmentRepository.findById(ENR_ID)).thenReturn(Optional.of(sampleEnrollment));

            EnrollmentResult result = useCase.execute(ENR_ID);

            assertThat(result.id()).isEqualTo(ENR_ID.value());
            assertThat(result.userId()).isEqualTo(USER_ID.value());
            assertThat(result.groupId()).isEqualTo(GROUP_ID.value());
            assertThat(result.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("should throw when enrollment not found")
        void shouldThrowWhenEnrollmentNotFound() {
            when(enrollmentRepository.findById(ENR_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(ENR_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Enrollment not found");
        }
    }

    // ======================== ListEnrollmentsByGroup ========================

    @Nested
    @DisplayName("ListEnrollmentsByGroupService")
    class ListEnrollmentsByGroupTests {

        private ListEnrollmentsByGroupUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ListEnrollmentsByGroupService(enrollmentRepository);
        }

        @Test
        @DisplayName("should return list of enrollments for group")
        void shouldReturnListOfEnrollmentsForGroup() {
            Enrollment e2 = Enrollment.restore(
                    new EnrollmentId("enr-002"),
                    new UserId("990e8400-e29b-41d4-a716-446655440006"),
                    GROUP_ID,
                    EnrollmentStatus.ACTIVE,
                    FIXED_NOW, FIXED_NOW
            );
            when(enrollmentRepository.findByGroupId(GROUP_ID))
                    .thenReturn(List.of(sampleEnrollment, e2));

            List<EnrollmentResult> results = useCase.execute(GROUP_ID);

            assertThat(results).hasSize(2);
            assertThat(results).extracting(EnrollmentResult::id)
                    .containsExactly("enr-001", "enr-002");
        }

        @Test
        @DisplayName("should return empty list when no enrollments")
        void shouldReturnEmptyListWhenNoEnrollments() {
            when(enrollmentRepository.findByGroupId(GROUP_ID)).thenReturn(List.of());

            List<EnrollmentResult> results = useCase.execute(GROUP_ID);

            assertThat(results).isEmpty();
        }
    }

    // ======================== DropEnrollment ========================

    @Nested
    @DisplayName("DropEnrollmentService")
    class DropEnrollmentTests {

        private DropEnrollmentUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DropEnrollmentService(enrollmentRepository, clock);
        }

        @Test
        @DisplayName("should drop active enrollment")
        void shouldDropActiveEnrollment() {
            when(enrollmentRepository.findById(ENR_ID)).thenReturn(Optional.of(sampleEnrollment));

            EnrollmentResult result = useCase.execute(ENR_ID);

            assertThat(result.status()).isEqualTo("DROPPED");
            verify(enrollmentRepository).save(any(Enrollment.class));
        }

        @Test
        @DisplayName("should be idempotent when already DROPPED")
        void shouldBeIdempotentWhenAlreadyDropped() {
            Enrollment alreadyDropped = Enrollment.restore(
                    ENR_ID, USER_ID, GROUP_ID,
                    EnrollmentStatus.DROPPED,
                    FIXED_NOW, LATER
            );
            when(enrollmentRepository.findById(ENR_ID)).thenReturn(Optional.of(alreadyDropped));

            EnrollmentResult result = useCase.execute(ENR_ID);

            assertThat(result.status()).isEqualTo("DROPPED");
            verify(enrollmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when enrollment not found")
        void shouldThrowWhenEnrollmentNotFound() {
            when(enrollmentRepository.findById(ENR_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(ENR_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Enrollment not found");
        }
    }
}
