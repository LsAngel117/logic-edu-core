package com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;
import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.academic.structure.model.StructureType;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.entity.AcademicStructureEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.repository.AcademicStructureJpaRepository;

import java.util.Optional;

public class AcademicStructureRepositoryAdapter implements AcademicStructureRepository {

    private final AcademicStructureJpaRepository jpa;

    public AcademicStructureRepositoryAdapter(AcademicStructureJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(AcademicStructure structure) {
        AcademicStructureEntity entity = mapToEntity(structure);
        jpa.save(entity);
    }

    @Override
    public Optional<AcademicStructure> findById(AcademicStructureId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public Optional<AcademicStructure> findBySchoolId(SchoolId schoolId) {
        return jpa.findBySchoolId(schoolId.value())
                .map(this::mapToDomain);
    }

    @Override
    public Optional<AcademicStructure> findActiveBySchoolId(SchoolId schoolId) {
        return jpa.findBySchoolIdAndActiveTrue(schoolId.value())
                .map(this::mapToDomain);
    }

    @Override
    public Optional<AcademicStructure> findLatestBySchoolId(SchoolId schoolId) {
        return jpa.findTopBySchoolIdOrderByVersionDesc(schoolId.value())
                .map(this::mapToDomain);
    }

    private AcademicStructureEntity mapToEntity(AcademicStructure s) {
        AcademicStructureEntity e = new AcademicStructureEntity();
        e.setId(s.getId().value());
        e.setSchoolId(s.getSchoolId().value());
        e.setStructureType(s.getStructureType().name());
        e.setLevelsCount(s.getLevelsCount());
        e.setPeriodsPerLevel(s.getPeriodsPerLevel());
        e.setEvaluationPeriodsPerPeriod(s.getEvaluationPeriodsPerPeriod());
        e.setSubjectsPerPeriod(s.getSubjectsPerPeriod());
        e.setHoursPerSubject(s.getHoursPerSubject());
        e.setActive(s.isActive());
        e.setVersion(s.getVersion());
        e.setCreatedAt(s.getCreatedAt());
        e.setUpdatedAt(s.getUpdatedAt());
        return e;
    }

    private AcademicStructure mapToDomain(AcademicStructureEntity e) {
        return AcademicStructure.restore(
                new AcademicStructureId(e.getId()),
                new SchoolId(e.getSchoolId()),
                StructureType.valueOf(e.getStructureType()),
                e.getLevelsCount(),
                e.getPeriodsPerLevel(),
                e.getEvaluationPeriodsPerPeriod(),
                e.getSubjectsPerPeriod(),
                e.getHoursPerSubject(),
                e.getVersion(),
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
