package com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectStatus;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.entity.SubjectEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.repository.SubjectJpaRepository;

import java.util.List;
import java.util.Optional;

public class SubjectRepositoryAdapter implements SubjectRepository {

    private final SubjectJpaRepository jpa;

    public SubjectRepositoryAdapter(SubjectJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Subject subject) {
        SubjectEntity entity = mapToEntity(subject);
        jpa.save(entity);
    }

    @Override
    public Optional<Subject> findById(SubjectId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<Subject> findBySchoolId(SchoolId schoolId) {
        return jpa.findBySchoolId(schoolId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public boolean existsBySchoolIdAndCode(SchoolId schoolId, String code) {
        return jpa.existsBySchoolIdAndCode(schoolId.value(), code);
    }

    private SubjectEntity mapToEntity(Subject subject) {
        SubjectEntity e = new SubjectEntity();
        e.setId(subject.getId().value());
        e.setSchoolId(subject.getSchoolId().value());
        e.setCode(subject.getCode());
        e.setName(subject.getName());
        e.setDescription(subject.getDescription());
        e.setHours(subject.getHours());
        e.setStatus(subject.getStatus().name());
        e.setCreatedAt(subject.getCreatedAt());
        e.setUpdatedAt(subject.getUpdatedAt());
        return e;
    }

    private Subject mapToDomain(SubjectEntity e) {
        return Subject.restore(
                new SubjectId(e.getId()),
                new SchoolId(e.getSchoolId()),
                e.getCode(),
                e.getName(),
                e.getDescription(),
                e.getHours(),
                SubjectStatus.valueOf(e.getStatus()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
