package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.application.school.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SchoolBeansConfig {

    @Bean
    public CreateSchoolService createSchoolService(
            SchoolRepository schoolRepository,
            Clock clock) {
        return new CreateSchoolService(schoolRepository, clock);
    }

    @Bean
    public GetSchoolService getSchoolService(SchoolRepository schoolRepository) {
        return new GetSchoolService(schoolRepository);
    }

    @Bean
    public ListSchoolsService listSchoolsService(SchoolRepository schoolRepository) {
        return new ListSchoolsService(schoolRepository);
    }

    @Bean
    public UpdateSchoolService updateSchoolService(
            SchoolRepository schoolRepository,
            Clock clock) {
        return new UpdateSchoolService(schoolRepository, clock);
    }

    @Bean
    public DeactivateSchoolService deactivateSchoolService(
            SchoolRepository schoolRepository,
            BranchRepository branchRepository,
            Clock clock) {
        return new DeactivateSchoolService(schoolRepository, branchRepository, clock);
    }
}
