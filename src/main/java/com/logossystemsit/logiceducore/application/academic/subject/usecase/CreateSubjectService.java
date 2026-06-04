package com.logossystemsit.logiceducore.application.academic.subject.usecase;

import com.logossystemsit.logiceducore.application.academic.subject.dto.command.CreateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.CreateSubjectUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class CreateSubjectService implements CreateSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final SchoolRepository schoolRepository;
    private final Clock clock;

    public CreateSubjectService(
            SubjectRepository subjectRepository,
            SchoolRepository schoolRepository,
            Clock clock
    ) {
        this.subjectRepository = subjectRepository;
        this.schoolRepository = schoolRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public SubjectResult execute(CreateSubjectCommand command) {
        if (schoolRepository.findById(command.schoolId()).isEmpty()) {
            throw new ResourceNotFoundException(ErrorCode.SCHOOL_NOT_FOUND,
                    "School not found: " + command.schoolId().value());
        }

        if (subjectRepository.existsBySchoolIdAndCode(command.schoolId(), command.code())) {
            throw new BusinessRuleException(ErrorCode.SUBJECT_ALREADY_EXISTS,
                    "Subject code " + command.code() + " already exists for school " + command.schoolId().value());
        }

        Subject subject = Subject.create(
                command.subjectId(),
                command.schoolId(),
                command.code(),
                command.name(),
                command.description(),
                command.hours(),
                clock.instant()
        );

        subjectRepository.save(subject);

        return SubjectResult.from(subject);
    }
}
