package com.logossystemsit.logiceducore.application.school.usecase;

import com.logossystemsit.logiceducore.application.school.dto.command.UpdateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.UpdateSchoolUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class UpdateSchoolService implements UpdateSchoolUseCase {

    private final SchoolRepository schoolRepository;
    private final Clock clock;

    public UpdateSchoolService(SchoolRepository schoolRepository, Clock clock) {
        this.schoolRepository = schoolRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public SchoolResult execute(UpdateSchoolCommand command) {
        School school = schoolRepository.findById(command.schoolId())
                .orElseThrow(() -> new IllegalArgumentException("School not found"));

        School updated = school.changeData(
                command.name(),
                command.code(),
                command.shortName(),
                command.description(),
                command.email(),
                command.phone(),
                command.address(),
                clock.instant()
        );

        schoolRepository.save(updated);

        return SchoolResult.from(updated);
    }
}
