package com.logossystemsit.logiceducore.application.academic.subject.usecase;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.DeactivateSubjectUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.Subject;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeactivateSubjectService implements DeactivateSubjectUseCase {

    private final SubjectRepository repository;
    private final Clock clock;

    public DeactivateSubjectService(SubjectRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public SubjectResult execute(SubjectId id) {
        Subject subject = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject not found: " + id.value()));

        Subject deactivated = subject.deactivate(clock.instant());
        repository.save(deactivated);

        return SubjectResult.from(deactivated);
    }
}
