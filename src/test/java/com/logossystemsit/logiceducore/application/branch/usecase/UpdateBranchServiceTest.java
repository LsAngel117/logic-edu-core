package com.logossystemsit.logiceducore.application.branch.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.branch.dto.command.UpdateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.UpdateBranchUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.shared.valueobject.City;
import com.logossystemsit.logiceducore.shared.valueobject.Country;
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
class UpdateBranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private Clock clock;

    private UpdateBranchUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final Instant UPDATE_NOW = Instant.parse("2025-07-01T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-1");
    private static final BranchId BRANCH_ID = BranchId.of("branch-1");

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(UPDATE_NOW);
        useCase = new UpdateBranchService(branchRepository, clock);
    }

    @Test
    void execute_shouldUpdateActiveBranchSuccessfully() {
        Branch branch = buildActiveBranch(BRANCH_ID, "Sede Norte", BranchType.SECONDARY);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));

        UpdateBranchCommand command = buildCommand(BranchType.SECONDARY, "Sede Norte Actualizada", "SNU-001");
        BranchResult result = useCase.execute(command);

        assertThat(result.name()).isEqualTo("Sede Norte Actualizada");
        assertThat(result.code()).isEqualTo("SNU-001");
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void execute_shouldRejectInactiveBranch() {
        Branch branch = buildInactiveBranch(BRANCH_ID, "Sede Cerrada");
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));

        UpdateBranchCommand command = buildCommand(BranchType.SECONDARY, "Sede Cerrada", "SC-001");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot modify an inactive branch");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldRejectDuplicateMainOnTypeChange() {
        Branch branch = buildActiveBranch(BRANCH_ID, "Sede Norte", BranchType.SECONDARY);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        // Another MAIN already exists
        when(branchRepository.countBySchoolIdAndType(SCHOOL_ID, BranchType.MAIN)).thenReturn(1);

        UpdateBranchCommand command = buildCommand(BranchType.MAIN, "Sede Norte", "SN-001");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("MAIN branch already exists");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldRejectDuplicateNameWithinSchool() {
        Branch branch = buildActiveBranch(BRANCH_ID, "Sede Norte", BranchType.SECONDARY);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchRepository.existsBySchoolIdAndName(SCHOOL_ID, BranchName.of("Sede Sur"))).thenReturn(true);

        UpdateBranchCommand command = buildCommand(BranchType.SECONDARY, "Sede Sur", "SS-001");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("name already exists");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldAllowSameNameWhenNotChanged() {
        Branch branch = buildActiveBranch(BRANCH_ID, "Sede Norte", BranchType.SECONDARY);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));

        // Same name, different code
        UpdateBranchCommand command = buildCommand(BranchType.SECONDARY, "Sede Norte", "SN-002");

        BranchResult result = useCase.execute(command);

        assertThat(result.code()).isEqualTo("SN-002");
        // Should NOT check for duplicate name since it's the same
        verify(branchRepository, never()).existsBySchoolIdAndName(any(), any());
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void execute_shouldThrowWhenBranchNotFound() {
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());

        UpdateBranchCommand command = buildCommand(BranchType.SECONDARY, "Sede X", "SX-001");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Branch not found");
    }

    private UpdateBranchCommand buildCommand(BranchType type, String name, String code) {
        return new UpdateBranchCommand(
                SCHOOL_ID,
                BRANCH_ID,
                BranchName.of(name),
                BranchCode.of(code),
                BranchShortName.of(name.substring(0, Math.min(name.length(), 10))),
                BranchDescription.of("Updated description"),
                BranchEmail.of("updated@branch.edu"),
                BranchPhone.of("+579876543"),
                BranchAddress.of("Carrera 45 #67-89"),
                new City("Medellín"),
                new Country("Colombia"),
                type
        );
    }

    private Branch buildActiveBranch(BranchId id, String name, BranchType type) {
        return Branch.restore(
                id,
                SCHOOL_ID,
                BranchName.of(name),
                BranchCode.of("BC-001"),
                BranchShortName.of(name.substring(0, Math.min(name.length(), 10))),
                BranchDescription.of("Description"),
                BranchEmail.of("branch@school.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.of("Calle 123 #45-67"),
                new City("Medellín"),
                new Country("Colombia"),
                type,
                Branch.Status.ACTIVE,
                FIXED_NOW,
                FIXED_NOW
        );
    }

    private Branch buildInactiveBranch(BranchId id, String name) {
        return Branch.restore(
                id,
                SCHOOL_ID,
                BranchName.of(name),
                BranchCode.of("BC-002"),
                BranchShortName.of(name.substring(0, Math.min(name.length(), 10))),
                BranchDescription.of("Description"),
                BranchEmail.of("closed@school.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.of("Calle 456"),
                new City("Medellín"),
                new Country("Colombia"),
                BranchType.SECONDARY,
                Branch.Status.INACTIVE,
                FIXED_NOW,
                FIXED_NOW
        );
    }
}
