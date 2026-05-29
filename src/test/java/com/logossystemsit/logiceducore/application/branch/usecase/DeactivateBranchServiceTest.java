package com.logossystemsit.logiceducore.application.branch.usecase;

import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.DeactivateBranchUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeactivateBranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private Clock clock;

    private DeactivateBranchUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final Instant DEACTIVATE_NOW = Instant.parse("2025-08-01T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-1");
    private static final BranchId BRANCH_ID = BranchId.of("branch-1");

    @BeforeEach
    void setUp() {
        useCase = new DeactivateBranchService(branchRepository, clock);
    }

    @Test
    void execute_shouldDeactivateSecondaryBranch() {
        Branch branch = buildBranch(BRANCH_ID, "Sede Norte", BranchType.SECONDARY, Branch.Status.ACTIVE);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(clock.instant()).thenReturn(DEACTIVATE_NOW);

        BranchResult result = useCase.execute(SCHOOL_ID, BRANCH_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void execute_shouldDeactivateMainWhenNoActiveSecondaries() {
        Branch branch = buildBranch(BRANCH_ID, "Sede Principal", BranchType.MAIN, Branch.Status.ACTIVE);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchRepository.countActiveBySchoolId(SCHOOL_ID)).thenReturn(1); // only this MAIN
        when(clock.instant()).thenReturn(DEACTIVATE_NOW);

        BranchResult result = useCase.execute(SCHOOL_ID, BRANCH_ID);

        assertThat(result.status()).isEqualTo("INACTIVE");
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void execute_shouldRejectMainDeactivationWithActiveSecondaries() {
        Branch branch = buildBranch(BRANCH_ID, "Sede Principal", BranchType.MAIN, Branch.Status.ACTIVE);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchRepository.countActiveBySchoolId(SCHOOL_ID)).thenReturn(3); // MAIN + 2 active secondaries

        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID, BRANCH_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot deactivate the main branch while there are active secondary branches");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldThrowWhenBranchNotFound() {
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID, BRANCH_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Branch not found");
    }

    @Test
    void execute_shouldThrowWhenBranchBelongsToOtherSchool() {
        Branch branch = buildBranch(BRANCH_ID, "Sede Ajena", BranchType.SECONDARY, Branch.Status.ACTIVE,
                new SchoolId("other-school"));
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));

        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID, BRANCH_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Branch not found");
    }

    @Test
    void execute_shouldThrowWhenBranchAlreadyInactive() {
        Branch branch = buildBranch(BRANCH_ID, "Sede Inactiva", BranchType.SECONDARY, Branch.Status.INACTIVE);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));

        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID, BRANCH_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already inactive");
    }

    private Branch buildBranch(BranchId id, String name, BranchType type, Branch.Status status) {
        return buildBranch(id, name, type, status, SCHOOL_ID);
    }

    private Branch buildBranch(BranchId id, String name, BranchType type, Branch.Status status, SchoolId schoolId) {
        return Branch.restore(
                id,
                schoolId,
                BranchName.of(name),
                BranchCode.of("BC-001"),
                BranchShortName.of(name.substring(0, Math.min(name.length(), 10))),
                BranchDescription.of("Description"),
                BranchEmail.of("branch@school.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.of("Calle 123 #45-67"),
                type,
                status,
                FIXED_NOW,
                FIXED_NOW
        );
    }
}
