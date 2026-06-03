package com.logossystemsit.logiceducore.application.academic.structure.port.out;

import com.logossystemsit.logiceducore.domain.academic.structure.model.AcademicStructure;
import com.logossystemsit.logiceducore.domain.academic.structure.model.valueobject.AcademicStructureId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.Optional;

public interface AcademicStructureRepository {

    void save(AcademicStructure structure);

    Optional<AcademicStructure> findById(AcademicStructureId id);

    Optional<AcademicStructure> findBySchoolId(SchoolId schoolId);

    Optional<AcademicStructure> findActiveBySchoolId(SchoolId schoolId);

    Optional<AcademicStructure> findLatestBySchoolId(SchoolId schoolId);
}
