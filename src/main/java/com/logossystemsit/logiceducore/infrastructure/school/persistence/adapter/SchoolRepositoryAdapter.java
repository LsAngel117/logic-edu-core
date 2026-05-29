package com.logossystemsit.logiceducore.infrastructure.school.persistence.adapter;

import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.entity.SchoolEntity;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.repository.SchoolJpaRepository;

import java.util.List;
import java.util.Optional;

public class SchoolRepositoryAdapter implements SchoolRepository {

    private final SchoolJpaRepository jpa;

    public SchoolRepositoryAdapter(SchoolJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(School school) {
        SchoolEntity entity = mapToEntity(school);
        jpa.save(entity);
    }

    @Override
    public Optional<School> findById(SchoolId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<School> findAll() {
        return jpa.findAll()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public boolean existsByName(SchoolName name) {
        return jpa.existsByName(name.value());
    }

    @Override
    public boolean existsByCode(SchoolCode code) {
        return jpa.existsByCode(code.value());
    }

    @Override
    public Optional<School> findByName(SchoolName name) {
        return jpa.findByName(name.value())
                .map(this::mapToDomain);
    }

    @Override
    public Optional<School> findByCode(SchoolCode code) {
        return jpa.findByCode(code.value())
                .map(this::mapToDomain);
    }

    private SchoolEntity mapToEntity(School school) {
        SchoolEntity e = new SchoolEntity();

        e.setId(school.getId().value());
        e.setName(school.getName().value());
        e.setCode(school.getCode().value());
        e.setShortName(school.getShortName().value());

        e.setDescription(school.getDescription().value().orElse(null));
        e.setEmail(school.getEmail() != null ? school.getEmail().value() : null);
        e.setPhone(school.getPhone() != null ? school.getPhone().value() : null);
        e.setAddress(school.getAddress().value().orElse(null));

        e.setStatus(school.getStatus());
        e.setCreatedAt(school.getCreatedAt());
        e.setUpdatedAt(school.getUpdatedAt());

        return e;
    }

    private School mapToDomain(SchoolEntity e) {
        SchoolDescription description = e.getDescription() != null
                ? SchoolDescription.of(e.getDescription())
                : SchoolDescription.empty();

        SchoolEmail email = e.getEmail() != null
                ? SchoolEmail.of(e.getEmail())
                : null;

        SchoolPhone phone = e.getPhone() != null
                ? SchoolPhone.of(e.getPhone())
                : null;

        SchoolAddress address = e.getAddress() != null
                ? SchoolAddress.of(e.getAddress())
                : SchoolAddress.empty();

        return School.restore(
                new SchoolId(e.getId()),
                new SchoolName(e.getName()),
                new SchoolCode(e.getCode()),
                SchoolShortName.of(e.getShortName()),
                description,
                email,
                phone,
                address,
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
