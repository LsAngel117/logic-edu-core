package com.logossystemsit.logiceducore.application.school.usecase;

import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;
import com.logossystemsit.logiceducore.application.school.port.in.ListSchoolsUseCase;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListSchoolsService implements ListSchoolsUseCase {

    private final SchoolRepository schoolRepository;

    public ListSchoolsService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolResult> execute() {
        return schoolRepository.findAll()
                .stream()
                .map(SchoolResult::from)
                .toList();
    }
}
