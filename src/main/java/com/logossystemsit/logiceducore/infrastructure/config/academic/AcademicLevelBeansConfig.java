package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.application.academic.level.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class AcademicLevelBeansConfig {
    @Bean public CreateAcademicLevelService createAcademicLevelService(AcademicLevelRepository r, Clock c) { return new CreateAcademicLevelService(r, c); }
    @Bean public GetAcademicLevelService getAcademicLevelService(AcademicLevelRepository r) { return new GetAcademicLevelService(r); }
    @Bean public ListAcademicLevelsBySchoolService listAcademicLevelsBySchoolService(AcademicLevelRepository r) { return new ListAcademicLevelsBySchoolService(r); }
    @Bean public UpdateAcademicLevelService updateAcademicLevelService(AcademicLevelRepository r, Clock c) { return new UpdateAcademicLevelService(r, c); }
    @Bean public DeactivateAcademicLevelService deactivateAcademicLevelService(AcademicLevelRepository r, Clock c) { return new DeactivateAcademicLevelService(r, c); }
}
