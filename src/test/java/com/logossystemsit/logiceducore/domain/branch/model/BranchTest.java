package com.logossystemsit.logiceducore.domain.branch.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;

import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BranchTest {

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-1");
    private static final BranchId BRANCH_ID = BranchId.generate();

    @Test
    void create_shouldCreateActiveBranch() {
        Branch branch = Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede Norte"),
                BranchCode.of("SN-001"),
                BranchShortName.of("S.Norte"),
                BranchDescription.of("Description"),
                BranchEmail.of("norte@branch.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.of("Calle 123 #45-67"),
                BranchType.MAIN,
                NOW
        );

        assertThat(branch.isActive()).isTrue();
        assertThat(branch.getName().value()).isEqualTo("Sede Norte");
        assertThat(branch.getType()).isEqualTo(BranchType.MAIN);
    }

    @Test
    void create_shouldRejectVirtualBranchWithAddress() {
        assertThatThrownBy(() -> Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede Virtual"),
                BranchCode.of("SV-001"),
                BranchShortName.of("S.Virtual"),
                BranchDescription.empty(),
                BranchEmail.of("virtual@branch.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.of("Calle 123"), // VIRTUAL + address = illegal
                BranchType.VIRTUAL,
                NOW
        )).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Virtual branch cannot have a physical address");
    }

    @Test
    void create_shouldRejectMainWithoutAddress() {
        assertThatThrownBy(() -> Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede Principal"),
                BranchCode.of("SP-001"),
                BranchShortName.of("S.Princ"),
                BranchDescription.empty(),
                BranchEmail.of("main@branch.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.empty(), // MAIN without address = illegal
                BranchType.MAIN,
                NOW
        )).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Physical branch must have an address");
    }

    @Test
    void create_shouldRejectSecondaryWithoutAddress() {
        assertThatThrownBy(() -> Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede Secundaria"),
                BranchCode.of("SS-001"),
                BranchShortName.of("S.Secun"),
                BranchDescription.empty(),
                BranchEmail.of("sec@branch.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.empty(), // SECONDARY without address = illegal
                BranchType.SECONDARY,
                NOW
        )).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Physical branch must have an address");
    }

    @Test
    void create_shouldAllowVirtualWithoutAddress() {
        Branch branch = Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede Virtual OK"),
                BranchCode.of("SVO-001"),
                BranchShortName.of("S.VirtualOK"),
                BranchDescription.empty(),
                null, // no email
                null, // no phone
                BranchAddress.empty(), // no address = OK for VIRTUAL
                BranchType.VIRTUAL,
                NOW
        );

        assertThat(branch.isActive()).isTrue();
        assertThat(branch.isVirtual()).isTrue();
        assertThat(branch.getAddress().isEmpty()).isTrue();
    }

    @Test
    void deactivate_shouldSetStatusToInactive() {
        Branch branch = Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede A Desactivar"),
                BranchCode.of("SAD-001"),
                BranchShortName.of("S.Desac"),
                BranchDescription.empty(),
                null,
                null,
                BranchAddress.of("Calle Principal"),
                BranchType.MAIN,
                NOW
        );

        Branch deactivated = branch.deactivate(NOW);

        assertThat(deactivated.isActive()).isFalse();
        assertThat(deactivated.getStatus()).isEqualTo(Branch.Status.INACTIVE);
    }

    @Test
    void deactivate_shouldBeIdempotent() {
        Branch branch = Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede B"),
                BranchCode.of("SB-001"),
                BranchShortName.of("S.B"),
                BranchDescription.empty(),
                null, null,
                BranchAddress.of("Calle Secundaria"),
                BranchType.SECONDARY,
                NOW
        );

        Branch alreadyInactive = branch.deactivate(NOW);
        Branch stillInactive = alreadyInactive.deactivate(NOW);

        assertThat(stillInactive.getStatus()).isEqualTo(Branch.Status.INACTIVE);
        // Should return same instance when already inactive
    }

    @Test
    void changeBasicInfo_shouldUpdateFields() {
        Branch branch = Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede Original"),
                BranchCode.of("SO-001"),
                BranchShortName.of("S.Orig"),
                BranchDescription.empty(),
                null, null,
                BranchAddress.of("Calle 1"),
                BranchType.MAIN,
                NOW
        );

        Instant later = NOW.plusSeconds(3600);
        Branch updated = branch.changeBasicInfo(
                BranchName.of("Sede Renombrada"),
                BranchCode.of("SR-001"),
                BranchShortName.of("S.Renom"),
                later
        );

        assertThat(updated.getName().value()).isEqualTo("Sede Renombrada");
        assertThat(updated.getCode().value()).isEqualTo("SR-001");
        assertThat(updated.getUpdatedAt()).isEqualTo(later);
    }

    @Test
    void changeBasicInfo_shouldRejectOnInactiveBranch() {
        Branch branch = Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede Inactiva"),
                BranchCode.of("SI-001"),
                BranchShortName.of("S.Inac"),
                BranchDescription.empty(),
                null, null,
                BranchAddress.of("Calle 2"),
                BranchType.SECONDARY,
                NOW
        );

        Branch deactivated = branch.deactivate(NOW);

        assertThatThrownBy(() -> deactivated.changeBasicInfo(
                BranchName.of("Nuevo Nombre"),
                BranchCode.of("NN-001"),
                BranchShortName.of("N.Nuevo"),
                NOW.plusSeconds(3600)
        )).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot modify an inactive branch");
    }

    @Test
    void changeType_shouldUpdateType() {
        Branch branch = Branch.create(
                BRANCH_ID, SCHOOL_ID,
                BranchName.of("Sede Cambiable"),
                BranchCode.of("SC-001"),
                BranchShortName.of("S.Camb"),
                BranchDescription.empty(),
                null, null,
                BranchAddress.of("Calle 3"),
                BranchType.SECONDARY,
                NOW
        );

        Branch updated = branch.changeType(BranchType.MAIN, NOW.plusSeconds(3600));

        assertThat(updated.getType()).isEqualTo(BranchType.MAIN);
    }
}
