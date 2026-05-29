package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.branch.usecase.*;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BranchBeansConfig {

    @Bean
    public CreateBranchService createBranchService(
            BranchRepository branchRepository,
            SchoolRepository schoolRepository,
            Clock clock) {
        return new CreateBranchService(branchRepository, schoolRepository, clock);
    }

    @Bean
    public GetBranchService getBranchService(BranchRepository branchRepository) {
        return new GetBranchService(branchRepository);
    }

    @Bean
    public ListBranchesBySchoolService listBranchesBySchoolService(BranchRepository branchRepository) {
        return new ListBranchesBySchoolService(branchRepository);
    }

    @Bean
    public UpdateBranchService updateBranchService(
            BranchRepository branchRepository,
            Clock clock) {
        return new UpdateBranchService(branchRepository, clock);
    }

    @Bean
    public DeactivateBranchService deactivateBranchService(
            BranchRepository branchRepository,
            Clock clock) {
        return new DeactivateBranchService(branchRepository, clock);
    }
}
