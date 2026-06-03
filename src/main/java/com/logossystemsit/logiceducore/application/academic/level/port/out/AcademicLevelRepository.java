package com.logossystemsit.logiceducore.application.academic.level.port.out;

import com.logossystemsit.logiceducore.domain.academic.level.model.AcademicLevel;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;
import java.util.Optional;

public interface AcademicLevelRepository {

    void save(AcademicLevel level);

    Optional<AcademicLevel> findById(AcademicLevelId id);

    List<AcademicLevel> findAllBySchoolId(SchoolId schoolId);

    boolean existsBySchoolIdAndNumber(SchoolId schoolId, int number);

    boolean existsActivePeriodsByLevelId(AcademicLevelId levelId);
}
