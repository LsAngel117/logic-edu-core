package com.logossystemsit.logiceducore.application.academic.subject.port.out;

import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository {

    void save(Subject subject);

    Optional<Subject> findById(SubjectId id);

    List<Subject> findBySchoolId(SchoolId schoolId);

    boolean existsBySchoolIdAndCode(SchoolId schoolId, String code);
}
