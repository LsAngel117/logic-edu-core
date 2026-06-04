package com.logossystemsit.logiceducore.application.branch.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.GetBranchUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    private GetBranchUseCase useCase;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-1");
    private static final SchoolId OTHER_SCHOOL_ID = new SchoolId("school-other");
    private static final BranchId BRANCH_ID = BranchId.of("branch-1");

    @BeforeEach
    void setUp() {
        useCase = new GetBranchService(branchRepository);
    }

    @Test
    void execute_shouldReturnBranchWhenFoundAndBelongsToSchool() {
        Branch branch = buildBranch(BRANCH_ID, SCHOOL_ID, "Sede Norte", BranchType.MAIN);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));

        BranchResult result = useCase.execute(SCHOOL_ID, BRANCH_ID);

        assertThat(result.id()).isEqualTo("branch-1");
        assertThat(result.name()).isEqualTo("Sede Norte");
        assertThat(result.schoolId()).isEqualTo(SCHOOL_ID.value());
        verify(branchRepository).findById(BRANCH_ID);
    }

    @Test
    void execute_shouldThrowWhenBranchNotFound() {
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID, BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Branch not found");
    }

    @Test
    void execute_shouldThrowWhenBranchBelongsToOtherSchool() {
        Branch branch = buildBranch(BRANCH_ID, OTHER_SCHOOL_ID, "Sede Sur", BranchType.SECONDARY);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));

        // Request from wrong school should return 404
        assertThatThrownBy(() -> useCase.execute(SCHOOL_ID, BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Branch not found");
    }

    private Branch buildBranch(BranchId id, SchoolId schoolId, String name, BranchType type) {
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
                Branch.Status.ACTIVE,
                NOW,
                NOW
        );
    }
}
