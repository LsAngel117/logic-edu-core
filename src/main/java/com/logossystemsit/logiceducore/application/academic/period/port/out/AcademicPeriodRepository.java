package com.logossystemsit.logiceducore.application.academic.period.port.out;

import com.logossystemsit.logiceducore.domain.academic.level.model.valueobject.AcademicLevelId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriod;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;

import java.util.List;
import java.util.Optional;

public interface AcademicPeriodRepository {
    void save(AcademicPeriod period);

    Optional<AcademicPeriod> findById(AcademicPeriodId id);

    List<AcademicPeriod> findByLevelId(AcademicLevelId levelId);

    boolean existsActiveEvaluationPeriodsByPeriodId(AcademicPeriodId periodId);
}
