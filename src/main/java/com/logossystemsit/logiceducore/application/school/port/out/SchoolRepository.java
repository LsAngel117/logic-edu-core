package com.logossystemsit.logiceducore.application.school.port.out;

import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolCode;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolName;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository {

    void save(School school);

    Optional<School> findById(SchoolId id);

    List<School> findAll();

    boolean existsByName(SchoolName name);

    boolean existsByCode(SchoolCode code);

    Optional<School> findByName(SchoolName name);

    Optional<School> findByCode(SchoolCode code);
}
