package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.evaluation.usecase.*;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class EvaluationPeriodBeansConfig {
    @Bean public CreateEvaluationPeriodService createEvaluationPeriodService(EvaluationPeriodRepository er, AcademicPeriodRepository pr, Clock c) { return new CreateEvaluationPeriodService(er, pr, c); }
    @Bean public GetEvaluationPeriodService getEvaluationPeriodService(EvaluationPeriodRepository r) { return new GetEvaluationPeriodService(r); }
    @Bean public ListEvaluationPeriodsByPeriodService listEvaluationPeriodsByPeriodService(EvaluationPeriodRepository r) { return new ListEvaluationPeriodsByPeriodService(r); }
    @Bean public UpdateEvaluationPeriodService updateEvaluationPeriodService(EvaluationPeriodRepository r, Clock c) { return new UpdateEvaluationPeriodService(r, c); }
    @Bean public DeactivateEvaluationPeriodService deactivateEvaluationPeriodService(EvaluationPeriodRepository r, Clock c) { return new DeactivateEvaluationPeriodService(r, c); }
}
