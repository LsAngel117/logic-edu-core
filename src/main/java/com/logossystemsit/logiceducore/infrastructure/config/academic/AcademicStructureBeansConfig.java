package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.application.academic.structure.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AcademicStructureBeansConfig {

    @Bean
    public CreateAcademicStructureService createAcademicStructureService(
            AcademicStructureRepository repository, Clock clock) {
        return new CreateAcademicStructureService(repository, clock);
    }

    @Bean
    public GetAcademicStructureService getAcademicStructureService(
            AcademicStructureRepository repository) {
        return new GetAcademicStructureService(repository);
    }

    @Bean
    public UpdateAcademicStructureService updateAcademicStructureService(
            AcademicStructureRepository repository, Clock clock) {
        return new UpdateAcademicStructureService(repository, clock);
    }

    @Bean
    public DeactivateAcademicStructureService deactivateAcademicStructureService(
            AcademicStructureRepository repository, Clock clock) {
        return new DeactivateAcademicStructureService(repository, clock);
    }
}
