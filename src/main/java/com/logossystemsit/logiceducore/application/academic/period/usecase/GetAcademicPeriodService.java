package com.logossystemsit.logiceducore.application.academic.period.usecase;

import com.logossystemsit.logiceducore.application.academic.period.dto.result.AcademicPeriodResult;
import com.logossystemsit.logiceducore.application.academic.period.port.in.GetAcademicPeriodUseCase;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;

public class GetAcademicPeriodService implements GetAcademicPeriodUseCase {

    private final AcademicPeriodRepository repository;

    public GetAcademicPeriodService(AcademicPeriodRepository repository) {
        this.repository = repository;
    }

    @Override
    public AcademicPeriodResult execute(AcademicPeriodId id) {
        AcademicPeriod period = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "AcademicPeriod not found: " + id.value()));

        return AcademicPeriodResult.from(period);
    }
}
