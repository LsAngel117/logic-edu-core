package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.period.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class AcademicPeriodBeansConfig {
    @Bean public CreateAcademicPeriodService createAcademicPeriodService(AcademicPeriodRepository r, Clock c) { return new CreateAcademicPeriodService(r, c); }
    @Bean public GetAcademicPeriodService getAcademicPeriodService(AcademicPeriodRepository r) { return new GetAcademicPeriodService(r); }
    @Bean public ListAcademicPeriodsByLevelService listAcademicPeriodsByLevelService(AcademicPeriodRepository r) { return new ListAcademicPeriodsByLevelService(r); }
    @Bean public UpdateAcademicPeriodService updateAcademicPeriodService(AcademicPeriodRepository r, Clock c) { return new UpdateAcademicPeriodService(r, c); }
    @Bean public DeactivateAcademicPeriodService deactivateAcademicPeriodService(AcademicPeriodRepository r, Clock c) { return new DeactivateAcademicPeriodService(r, c); }
}
