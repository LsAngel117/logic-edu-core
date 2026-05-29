package com.logossystemsit.logiceducore.application.school.usecase;

import com.logossystemsit.logiceducore.application.school.dto.command.CreateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.CreateSchoolUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

public class CreateSchoolService implements CreateSchoolUseCase {

    private final SchoolRepository schoolRepository;
    private final Clock clock;

    public CreateSchoolService(SchoolRepository schoolRepository, Clock clock) {
        this.schoolRepository = schoolRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public SchoolResult execute(CreateSchoolCommand command) {
        if (schoolRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("School name already exists");
        }

        if (schoolRepository.existsByCode(command.code())) {
            throw new IllegalArgumentException("School code already exists");
        }

        Instant now = clock.instant();

        School school = School.create(
                command.schoolId(),
                command.name(),
                command.code(),
                command.shortName(),
                command.description(),
                command.email(),
                command.phone(),
                command.address(),
                now
        );

        schoolRepository.save(school);

        return SchoolResult.from(school);
    }
}
