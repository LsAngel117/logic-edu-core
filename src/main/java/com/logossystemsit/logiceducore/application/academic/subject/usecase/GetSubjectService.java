package com.logossystemsit.logiceducore.application.academic.subject.usecase;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.GetSubjectUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import org.springframework.transaction.annotation.Transactional;

public class GetSubjectService implements GetSubjectUseCase {

    private final SubjectRepository repository;

    public GetSubjectService(SubjectRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResult execute(SubjectId id) {
        return repository.findById(id)
                .map(SubjectResult::from)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subject not found: " + id.value()));
    }
}
