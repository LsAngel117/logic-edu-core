package com.logossystemsit.logiceducore.application.academic.evaluation.port.out;

import com.logossystemsit.logiceducore.domain.academic.evaluation.model.EvaluationPeriod;
import com.logossystemsit.logiceducore.domain.academic.evaluation.model.valueobject.EvaluationPeriodId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EvaluationPeriodRepository {
    void save(EvaluationPeriod period);

    Optional<EvaluationPeriod> findById(EvaluationPeriodId id);

    List<EvaluationPeriod> findByPeriodId(AcademicPeriodId periodId);

    BigDecimal sumWeightsByPeriodId(AcademicPeriodId periodId);
}
