package com.logossystemsit.logiceducore.application.academic.subject.usecase;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.application.academic.subject.port.in.ListSubjectsBySchoolUseCase;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListSubjectsBySchoolService implements ListSubjectsBySchoolUseCase {

    private final SubjectRepository repository;

    public ListSubjectsBySchoolService(SubjectRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResult> execute(SchoolId schoolId) {
        return repository.findBySchoolId(schoolId).stream()
                .map(SubjectResult::from)
                .toList();
    }
}
