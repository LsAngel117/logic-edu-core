package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.adapter.BranchRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.repository.BranchJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.adapter.MembershipRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.repository.MembershipJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.adapter.SchoolRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.repository.SchoolJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.adapter.UserRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.repository.UserJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {

    @Bean
    public UserRepository userRepositoryAdapter(UserJpaRepository jpa) {
        return new UserRepositoryAdapter(jpa);
    }

    @Bean
    public MembershipRepository membershipRepositoryAdapter(MembershipJpaRepository jpa) {
        return new MembershipRepositoryAdapter(jpa);
    }

    @Bean
    public SchoolRepository schoolRepositoryAdapter(SchoolJpaRepository jpa) {
        return new SchoolRepositoryAdapter(jpa);
    }

    @Bean
    public BranchRepository branchRepositoryAdapter(BranchJpaRepository jpa) {
        return new BranchRepositoryAdapter(jpa);
    }
}
