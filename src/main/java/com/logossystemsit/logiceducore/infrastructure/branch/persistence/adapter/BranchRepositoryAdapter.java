package com.logossystemsit.logiceducore.infrastructure.branch.persistence.adapter;

import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.domain.branch.model.Branch;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.shared.valueobject.City;
import com.logossystemsit.logiceducore.shared.valueobject.Country;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.entity.BranchEntity;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.repository.BranchJpaRepository;

import java.util.List;
import java.util.Optional;

public class BranchRepositoryAdapter implements BranchRepository {

    private final BranchJpaRepository jpa;

    public BranchRepositoryAdapter(BranchJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Branch branch) {
        BranchEntity entity = mapToEntity(branch);
        jpa.save(entity);
    }

    @Override
    public Optional<Branch> findById(BranchId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<Branch> findBySchoolId(SchoolId schoolId) {
        return jpa.findBySchoolId(schoolId.value())
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public boolean existsBySchoolIdAndName(SchoolId schoolId, BranchName name) {
        return jpa.existsBySchoolIdAndName(schoolId.value(), name.value());
    }

    @Override
    public int countBySchoolIdAndType(SchoolId schoolId, BranchType type) {
        return jpa.countBySchoolIdAndType(schoolId.value(), type);
    }

    @Override
    public int countActiveBySchoolId(SchoolId schoolId) {
        return jpa.countActiveBySchoolId(schoolId.value(), Branch.Status.ACTIVE);
    }

    @Override
    public boolean existsActiveBySchoolId(SchoolId schoolId) {
        return jpa.existsActiveBySchoolId(schoolId.value(), Branch.Status.ACTIVE);
    }

    private BranchEntity mapToEntity(Branch branch) {
        BranchEntity e = new BranchEntity();

        e.setId(branch.getId().value());
        e.setSchoolId(branch.getSchoolId().value());
        e.setName(branch.getName().value());
        e.setCode(branch.getCode().value());
        e.setShortName(branch.getShortName().value());

        e.setDescription(branch.getDescription().value().orElse(null));
        e.setEmail(branch.getEmail() != null ? branch.getEmail().value() : null);
        e.setPhone(branch.getPhone() != null ? branch.getPhone().value() : null);
        e.setAddress(branch.getAddress().value().orElse(null));

        e.setCity(branch.getCity().value());
        e.setCountry(branch.getCountry().value());

        e.setType(branch.getType());
        e.setStatus(branch.getStatus());
        e.setCreatedAt(branch.getCreatedAt());
        e.setUpdatedAt(branch.getUpdatedAt());

        return e;
    }

    private Branch mapToDomain(BranchEntity e) {
        BranchDescription description = e.getDescription() != null
                ? BranchDescription.of(e.getDescription())
                : BranchDescription.empty();

        BranchEmail email = e.getEmail() != null
                ? BranchEmail.of(e.getEmail())
                : null;

        BranchPhone phone = e.getPhone() != null
                ? BranchPhone.of(e.getPhone())
                : null;

        BranchAddress address = e.getAddress() != null
                ? BranchAddress.of(e.getAddress())
                : BranchAddress.empty();

        return Branch.restore(
                BranchId.of(e.getId()),
                new SchoolId(e.getSchoolId()),
                BranchName.of(e.getName()),
                BranchCode.of(e.getCode()),
                BranchShortName.of(e.getShortName()),
                description,
                email,
                phone,
                address,
                new City(e.getCity()),
                new Country(e.getCountry()),
                e.getType(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
