package com.logossystemsit.logiceducore.application.branch.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.application.branch.dto.command.CreateBranchCommand;
import com.logossystemsit.logiceducore.application.branch.dto.result.BranchResult;
import com.logossystemsit.logiceducore.application.branch.port.in.CreateBranchUseCase;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
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
class CreateBranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private Clock clock;

    private CreateBranchUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-1");
    private static final BranchId BRANCH_ID = BranchId.generate();
    private static final BranchName NAME = BranchName.of("Sede Norte");
    private static final BranchCode CODE = BranchCode.of("SN-001");
    private static final BranchShortName SHORT_NAME = BranchShortName.of("S.Norte");
    private static final BranchDescription DESCRIPTION = BranchDescription.of("Sede principal norte");
    private static final BranchEmail EMAIL = BranchEmail.of("norte@branch.edu");
    private static final BranchPhone PHONE = BranchPhone.of("+571234567");
    private static final BranchAddress ADDRESS = BranchAddress.of("Calle 123 #45-67");

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(FIXED_NOW);
        useCase = new CreateBranchService(branchRepository, schoolRepository, clock);
    }

    @Test
    void execute_shouldCreateBranchWhenSchoolActiveAndNoConflicts() {
        School school = buildActiveSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsBySchoolIdAndName(SCHOOL_ID, NAME)).thenReturn(false);
        when(branchRepository.countBySchoolIdAndType(SCHOOL_ID, BranchType.MAIN)).thenReturn(0);

        CreateBranchCommand command = buildCommand(BranchType.MAIN);
        BranchResult result = useCase.execute(command);

        assertThat(result.id()).isEqualTo(BRANCH_ID.value());
        assertThat(result.name()).isEqualTo("Sede Norte");
        assertThat(result.code()).isEqualTo("SN-001");
        assertThat(result.type()).isEqualTo("MAIN");
        assertThat(result.status()).isEqualTo("ACTIVE");
        assertThat(result.schoolId()).isEqualTo(SCHOOL_ID.value());

        verify(branchRepository).existsBySchoolIdAndName(SCHOOL_ID, NAME);
        verify(branchRepository).countBySchoolIdAndType(SCHOOL_ID, BranchType.MAIN);
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void execute_shouldCreateSecondaryBranchSuccessfully() {
        School school = buildActiveSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsBySchoolIdAndName(SCHOOL_ID, NAME)).thenReturn(false);

        CreateBranchCommand command = buildCommand(BranchType.SECONDARY);
        BranchResult result = useCase.execute(command);

        assertThat(result.type()).isEqualTo("SECONDARY");
        // SECONDARY type doesn't check MAIN uniqueness
        verify(branchRepository, never()).countBySchoolIdAndType(any(), any());
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void execute_shouldRejectInactiveSchool() {
        School school = buildInactiveSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));

        CreateBranchCommand command = buildCommand(BranchType.SECONDARY);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("inactive");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldRejectDuplicateMainBranch() {
        School school = buildActiveSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.countBySchoolIdAndType(SCHOOL_ID, BranchType.MAIN)).thenReturn(1);

        CreateBranchCommand command = buildCommand(BranchType.MAIN);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("MAIN branch already exists");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldRejectDuplicateNameWithinSchool() {
        School school = buildActiveSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsBySchoolIdAndName(SCHOOL_ID, NAME)).thenReturn(true);

        CreateBranchCommand command = buildCommand(BranchType.SECONDARY);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("name already exists");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldRejectVirtualBranchWithAddress() {
        School school = buildActiveSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsBySchoolIdAndName(SCHOOL_ID, NAME)).thenReturn(false);

        CreateBranchCommand command = buildCommand(BranchType.VIRTUAL);

        // Domain validates VIRTUAL + address → throws IllegalStateException
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Virtual branch cannot have a physical address");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldRejectUnknownSchoolId() {
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.empty());

        CreateBranchCommand command = buildCommand(BranchType.MAIN);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("School not found");

        verify(branchRepository, never()).save(any());
    }

    @Test
    void execute_shouldSetCorrectTimestamps() {
        School school = buildActiveSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsBySchoolIdAndName(SCHOOL_ID, NAME)).thenReturn(false);

        CreateBranchCommand command = buildCommand(BranchType.MAIN);
        BranchResult result = useCase.execute(command);

        assertThat(result.createdAt()).isEqualTo(FIXED_NOW);
        assertThat(result.updatedAt()).isEqualTo(FIXED_NOW);
    }

    @Test
    void execute_shouldCreateBranchWithoutOptionalFields() {
        School school = buildActiveSchool();
        when(schoolRepository.findById(SCHOOL_ID)).thenReturn(Optional.of(school));
        when(branchRepository.existsBySchoolIdAndName(SCHOOL_ID, NAME)).thenReturn(false);

        CreateBranchCommand command = new CreateBranchCommand(
                BRANCH_ID, SCHOOL_ID, NAME, CODE, SHORT_NAME,
                BranchDescription.empty(), null, null, BranchAddress.empty(), BranchType.VIRTUAL
        );

        BranchResult result = useCase.execute(command);

        assertThat(result.description()).isNull();
        assertThat(result.email()).isNull();
        assertThat(result.phone()).isNull();
        assertThat(result.address()).isNull();
        verify(branchRepository).save(any(Branch.class));
    }

    private CreateBranchCommand buildCommand(BranchType type) {
        return new CreateBranchCommand(
                BRANCH_ID, SCHOOL_ID, NAME, CODE, SHORT_NAME, DESCRIPTION, EMAIL, PHONE, ADDRESS, type
        );
    }

    private School buildActiveSchool() {
        return School.restore(
                SCHOOL_ID,
                new SchoolName("Colegio Andino"),
                new SchoolCode("CA-001"),
                SchoolShortName.of("Col.Andino"),
                SchoolDescription.of("Colegio bilingue"),
                SchoolEmail.of("info@andino.edu"),
                SchoolPhone.of("+571234567"),
                SchoolAddress.of("Calle 123"),
                School.Status.ACTIVE,
                FIXED_NOW,
                FIXED_NOW
        );
    }

    private School buildInactiveSchool() {
        return School.restore(
                SCHOOL_ID,
                new SchoolName("Colegio Cerrado"),
                new SchoolCode("CC-001"),
                SchoolShortName.of("Col.Cerrado"),
                SchoolDescription.of("Colegio cerrado"),
                SchoolEmail.of("cerrado@school.edu"),
                SchoolPhone.of("+571234567"),
                SchoolAddress.of("Calle 456"),
                School.Status.INACTIVE,
                FIXED_NOW,
                FIXED_NOW
        );
    }
}
