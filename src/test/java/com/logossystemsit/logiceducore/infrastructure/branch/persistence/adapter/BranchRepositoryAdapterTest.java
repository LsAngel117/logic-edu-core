package com.logossystemsit.logiceducore.infrastructure.branch.persistence.adapter;

import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.shared.valueobject.City;
import com.logossystemsit.logiceducore.shared.valueobject.Country;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.entity.BranchEntity;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.repository.BranchJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchRepositoryAdapterTest {

    @Mock
    private BranchJpaRepository jpa;

    private BranchRepository adapter;

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final SchoolId SCHOOL_ID = new SchoolId("school-1");

    @BeforeEach
    void setUp() {
        adapter = new BranchRepositoryAdapter(jpa);
    }

    @Test
    void save_shouldMapToEntityAndDelegateToJpaRepository() {
        Branch branch = buildBranch("branch-1", "Sede Norte", "SN-001", BranchType.MAIN, Branch.Status.ACTIVE);

        adapter.save(branch);

        ArgumentCaptor<BranchEntity> captor = ArgumentCaptor.forClass(BranchEntity.class);
        verify(jpa).save(captor.capture());
        BranchEntity entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo("branch-1");
        assertThat(entity.getName()).isEqualTo("Sede Norte");
        assertThat(entity.getCode()).isEqualTo("SN-001");
        assertThat(entity.getType()).isEqualTo(BranchType.MAIN);
        assertThat(entity.getSchoolId()).isEqualTo(SCHOOL_ID.value());
    }

    @Test
    void findById_shouldReturnDomainObjectWhenFound() {
        BranchEntity entity = buildEntity("branch-2", "Sede Sur", "SS-002", BranchType.SECONDARY);
        when(jpa.findById("branch-2")).thenReturn(Optional.of(entity));

        Optional<Branch> result = adapter.findById(BranchId.of("branch-2"));

        assertThat(result).isPresent();
        assertThat(result.get().getId().value()).isEqualTo("branch-2");
        assertThat(result.get().getName().value()).isEqualTo("Sede Sur");
        verify(jpa).findById("branch-2");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(jpa.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Branch> result = adapter.findById(BranchId.of("nonexistent"));

        assertThat(result).isEmpty();
        verify(jpa).findById("nonexistent");
    }

    @Test
    void findBySchoolId_shouldReturnListOfDomainObjects() {
        BranchEntity e1 = buildEntity("branch-3", "Sede A", "SA-003", BranchType.MAIN);
        BranchEntity e2 = buildEntity("branch-4", "Sede B", "SB-004", BranchType.SECONDARY);
        when(jpa.findBySchoolId(SCHOOL_ID.value())).thenReturn(List.of(e1, e2));

        List<Branch> result = adapter.findBySchoolId(SCHOOL_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName().value()).isEqualTo("Sede A");
        assertThat(result.get(1).getName().value()).isEqualTo("Sede B");
        verify(jpa).findBySchoolId(SCHOOL_ID.value());
    }

    @Test
    void existsBySchoolIdAndName_shouldDelegateToJpaRepository() {
        BranchName name = BranchName.of("Sede Test");
        when(jpa.existsBySchoolIdAndName(SCHOOL_ID.value(), "Sede Test")).thenReturn(true);

        boolean result = adapter.existsBySchoolIdAndName(SCHOOL_ID, name);

        assertThat(result).isTrue();
        verify(jpa).existsBySchoolIdAndName(SCHOOL_ID.value(), "Sede Test");
    }

    @Test
    void existsBySchoolIdAndName_shouldReturnFalseWhenNotExists() {
        BranchName name = BranchName.of("Inexistente");
        when(jpa.existsBySchoolIdAndName(SCHOOL_ID.value(), "Inexistente")).thenReturn(false);

        boolean result = adapter.existsBySchoolIdAndName(SCHOOL_ID, name);

        assertThat(result).isFalse();
        verify(jpa).existsBySchoolIdAndName(SCHOOL_ID.value(), "Inexistente");
    }

    @Test
    void countBySchoolIdAndType_shouldDelegateToJpaRepository() {
        when(jpa.countBySchoolIdAndType(SCHOOL_ID.value(), BranchType.MAIN)).thenReturn(1);

        int result = adapter.countBySchoolIdAndType(SCHOOL_ID, BranchType.MAIN);

        assertThat(result).isEqualTo(1);
        verify(jpa).countBySchoolIdAndType(SCHOOL_ID.value(), BranchType.MAIN);
    }

    @Test
    void countBySchoolIdAndType_shouldReturnZeroWhenNone() {
        when(jpa.countBySchoolIdAndType(SCHOOL_ID.value(), BranchType.VIRTUAL)).thenReturn(0);

        int result = adapter.countBySchoolIdAndType(SCHOOL_ID, BranchType.VIRTUAL);

        assertThat(result).isZero();
        verify(jpa).countBySchoolIdAndType(SCHOOL_ID.value(), BranchType.VIRTUAL);
    }

    @Test
    void countActiveBySchoolId_shouldDelegateToJpaRepository() {
        when(jpa.countActiveBySchoolId(SCHOOL_ID.value(), Branch.Status.ACTIVE)).thenReturn(3);

        int result = adapter.countActiveBySchoolId(SCHOOL_ID);

        assertThat(result).isEqualTo(3);
        verify(jpa).countActiveBySchoolId(SCHOOL_ID.value(), Branch.Status.ACTIVE);
    }

    @Test
    void existsActiveBySchoolId_shouldReturnTrueWhenActiveBranchesExist() {
        when(jpa.existsActiveBySchoolId(SCHOOL_ID.value(), Branch.Status.ACTIVE)).thenReturn(true);

        boolean result = adapter.existsActiveBySchoolId(SCHOOL_ID);

        assertThat(result).isTrue();
        verify(jpa).existsActiveBySchoolId(SCHOOL_ID.value(), Branch.Status.ACTIVE);
    }

    @Test
    void existsActiveBySchoolId_shouldReturnFalseWhenNoActiveBranches() {
        when(jpa.existsActiveBySchoolId(SCHOOL_ID.value(), Branch.Status.ACTIVE)).thenReturn(false);

        boolean result = adapter.existsActiveBySchoolId(SCHOOL_ID);

        assertThat(result).isFalse();
        verify(jpa).existsActiveBySchoolId(SCHOOL_ID.value(), Branch.Status.ACTIVE);
    }

    @Test
    void save_shouldHandleOptionalFields() {
        BranchEntity entityWithNulls = buildEntity("branch-5", "Sede Minimal", "SM-005", BranchType.VIRTUAL);
        entityWithNulls.setDescription(null);
        entityWithNulls.setEmail(null);
        entityWithNulls.setPhone(null);
        entityWithNulls.setAddress(null);

        when(jpa.findById("branch-5")).thenReturn(Optional.of(entityWithNulls));

        Optional<Branch> result = adapter.findById(BranchId.of("branch-5"));

        assertThat(result).isPresent();
        assertThat(result.get().getDescription().isEmpty()).isTrue();
        assertThat(result.get().getAddress().isEmpty()).isTrue();
    }

    private Branch buildBranch(String id, String name, String code, BranchType type, Branch.Status status) {
        return Branch.restore(
                BranchId.of(id),
                SCHOOL_ID,
                BranchName.of(name),
                BranchCode.of(code),
                BranchShortName.of(name.substring(0, Math.min(name.length(), 10))),
                BranchDescription.of("Description of " + name),
                BranchEmail.of(code.toLowerCase().replace("-", "") + "@branch.edu"),
                BranchPhone.of("+571234567"),
                BranchAddress.of("Calle 123 #45-67"),
                new City("Medellín"),
                new Country("Colombia"),
                type,
                status,
                NOW,
                NOW
        );
    }

    private BranchEntity buildEntity(String id, String name, String code, BranchType type) {
        BranchEntity e = new BranchEntity();
        e.setId(id);
        e.setSchoolId(SCHOOL_ID.value());
        e.setName(name);
        e.setCode(code);
        e.setShortName(name.substring(0, Math.min(name.length(), 10)));
        e.setDescription("Description of " + name);
        e.setEmail(code.toLowerCase().replace("-", "") + "@branch.edu");
        e.setPhone("+571234567");
        e.setAddress("Calle 123 #45-67");
        e.setCity("Medellín");
        e.setCountry("Colombia");
        e.setType(type);
        e.setStatus(Branch.Status.ACTIVE);
        e.setCreatedAt(NOW);
        e.setUpdatedAt(NOW);
        return e;
    }
}
