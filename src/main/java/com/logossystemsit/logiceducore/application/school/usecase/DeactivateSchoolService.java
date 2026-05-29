package com.logossystemsit.logiceducore.application.school.usecase;

import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.DeactivateSchoolUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeactivateSchoolService implements DeactivateSchoolUseCase {

    private final SchoolRepository schoolRepository;
    private final Clock clock;

    public DeactivateSchoolService(SchoolRepository schoolRepository, Clock clock) {
        this.schoolRepository = schoolRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public SchoolResult execute(SchoolId schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found"));

        // TODO PR#2: Check no active branches exist before deactivating

        School deactivated = school.deactivate(clock.instant());

        schoolRepository.save(deactivated);

        return SchoolResult.from(deactivated);
    }
}
