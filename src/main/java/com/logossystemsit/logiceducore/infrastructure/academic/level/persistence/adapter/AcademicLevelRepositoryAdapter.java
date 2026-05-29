package com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevelStatus;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.entity.AcademicLevelEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.repository.AcademicLevelJpaRepository;

import java.util.List;
import java.util.Optional;

public class AcademicLevelRepositoryAdapter implements AcademicLevelRepository {

    private final AcademicLevelJpaRepository jpa;

    public AcademicLevelRepositoryAdapter(AcademicLevelJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(AcademicLevel level) {
        AcademicLevelEntity entity = mapToEntity(level);
        jpa.save(entity);
    }

    @Override
    public Optional<AcademicLevel> findById(AcademicLevelId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<AcademicLevel> findAllBySchoolId(SchoolId schoolId) {
        return jpa.findAllBySchoolId(schoolId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public boolean existsBySchoolIdAndNumber(SchoolId schoolId, int number) {
        return jpa.existsBySchoolIdAndNumber(schoolId.value(), number);
    }

    @Override
    public boolean existsActivePeriodsByLevelId(AcademicLevelId levelId) {
        return jpa.existsActivePeriodsByLevelId(levelId.value());
    }

    private AcademicLevelEntity mapToEntity(AcademicLevel level) {
        AcademicLevelEntity e = new AcademicLevelEntity();
        e.setId(level.getId().value());
        e.setSchoolId(level.getSchoolId().value());
        e.setName(level.getName());
        e.setNumber(level.getNumber());
        e.setStatus(level.getStatus().name());
        e.setCreatedAt(level.getCreatedAt());
        e.setUpdatedAt(level.getUpdatedAt());
        return e;
    }

    private AcademicLevel mapToDomain(AcademicLevelEntity e) {
        return AcademicLevel.restore(
                new AcademicLevelId(e.getId()),
                new SchoolId(e.getSchoolId()),
                e.getName(),
                e.getNumber(),
                AcademicLevelStatus.valueOf(e.getStatus()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
