package com.logossystemsit.logiceducore.application.school.usecase;

import com.logossystemsit.logiceducore.application.school.dto.command.UpdateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.UpdateSchoolUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.school.model.School;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SCHOOL_NOT_FOUND, "School not found"));

        School updated = school.changeData(
                command.name(),
                command.code(),
                command.shortName(),
                command.description(),
                command.email(),
                command.phone(),
                command.address(),
                command.city(),
                command.country(),
                clock.instant()
        );

        schoolRepository.save(updated);

        return SchoolResult.from(updated);
    }
}
