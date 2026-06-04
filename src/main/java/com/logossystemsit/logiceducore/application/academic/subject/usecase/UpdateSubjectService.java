package com.logossystemsit.logiceducore.application.academic.subject.usecase;

import com.logossystemsit.logiceducore.application.academic.subject.dto.command.UpdateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.UpdateSubjectUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectStatus;
import com.logossystemsit.logiceducore.shared.errors.ErrorCode;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class UpdateSubjectService implements UpdateSubjectUseCase {

    private final SubjectRepository repository;
    private final Clock clock;

    public UpdateSubjectService(SubjectRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public SubjectResult execute(UpdateSubjectCommand command) {
        Subject current = repository.findById(command.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SUBJECT_NOT_FOUND,
                        "Subject not found: " + command.subjectId().value()));

        if (current.getStatus() == SubjectStatus.INACTIVE) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Cannot modify an inactive subject");
        }

        if (!command.code().equals(current.getCode())) {
            if (repository.existsBySchoolIdAndCode(command.schoolId(), command.code())) {
                throw new BusinessRuleException(ErrorCode.SUBJECT_ALREADY_EXISTS,
                        "Subject code " + command.code() + " already exists for school " + command.schoolId().value());
            }
        }

        Subject updated = current.changeData(
                command.code(), command.name(), command.description(), command.hours(),
                clock.instant()
        );

        repository.save(updated);

        return SubjectResult.from(updated);
    }
}
