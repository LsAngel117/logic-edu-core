package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.ListAcademicPeriodsByLevelUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;

import java.util.List;

public class ListAcademicPeriodsByLevelService implements ListAcademicPeriodsByLevelUseCase {

    private final AcademicPeriodRepository repository;

    public ListAcademicPeriodsByLevelService(AcademicPeriodRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AcademicPeriodResult> execute(AcademicLevelId levelId) {
        List<AcademicPeriod> periods = repository.findByLevelId(levelId);

        return periods.stream()
                .map(AcademicPeriodResult::from)
                .toList();
    }
}
