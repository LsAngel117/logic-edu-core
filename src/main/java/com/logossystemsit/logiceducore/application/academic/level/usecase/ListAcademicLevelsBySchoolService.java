package com.logossystemsit.logiceducore.application.academic.level.usecase;

import com.logossystemsit.logiceducore.application.academic.level.dto.result.AcademicLevelResult;
import com.logossystemsit.logiceducore.application.academic.level.port.in.ListAcademicLevelsBySchoolUseCase;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;

public class ListAcademicLevelsBySchoolService implements ListAcademicLevelsBySchoolUseCase {

    private final AcademicLevelRepository repository;

    public ListAcademicLevelsBySchoolService(AcademicLevelRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AcademicLevelResult> execute(SchoolId schoolId) {
        return repository.findAllBySchoolId(schoolId).stream()
                .map(AcademicLevelResult::from)
                .toList();
    }
}
