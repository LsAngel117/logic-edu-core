package com.logossystemsit.logiceducore.application.branch.usecase;

import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.ListBranchesBySchoolUseCase;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListBranchesBySchoolServiceTest {

    @Mock
    private BranchRepository branchRepository;

    private ListBranchesBySchoolUseCase useCase;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-1");

    @BeforeEach
    void setUp() {
        useCase = new ListBranchesBySchoolService(branchRepository);
    }

    @Test
    void execute_shouldReturnBranchesForSchool() {
        Branch b1 = buildBranch(BranchId.of("b1"), "Sede Norte", BranchType.MAIN);
        Branch b2 = buildBranch(BranchId.of("b2"), "Sede Sur", BranchType.SECONDARY);
        Branch b3 = buildBranch(BranchId.of("b3"), "Sede Virtual", BranchType.VIRTUAL);

        when(branchRepository.findBySchoolId(SCHOOL_ID)).thenReturn(List.of(b1, b2, b3));

        List<BranchResult> results = useCase.execute(SCHOOL_ID);

        assertThat(results).hasSize(3);
        assertThat(results.get(0).name()).isEqualTo("Sede Norte");
        assertThat(results.get(1).name()).isEqualTo("Sede Sur");
        assertThat(results.get(2).name()).isEqualTo("Sede Virtual");
        verify(branchRepository).findBySchoolId(SCHOOL_ID);
    }

    @Test
    void execute_shouldReturnEmptyListWhenNoBranches() {
        when(branchRepository.findBySchoolId(SCHOOL_ID)).thenReturn(Collections.emptyList());

        List<BranchResult> results = useCase.execute(SCHOOL_ID);

        assertThat(results).isEmpty();
        verify(branchRepository).findBySchoolId(SCHOOL_ID);
    }

    private Branch buildBranch(BranchId id, String name, BranchType type) {
        return Branch.restore(
                id,
                SCHOOL_ID,
                BranchName.of(name),
                BranchCode.of("BC-001"),
                BranchShortName.of(name.substring(0, Math.min(name.length(), 10))),
                BranchDescription.of("Description"),
                BranchEmail.of("branch@school.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.of(type != BranchType.VIRTUAL ? "Calle 123 #45-67" : null),
                type,
                Branch.Status.ACTIVE,
                NOW,
                NOW
        );
    }
}
