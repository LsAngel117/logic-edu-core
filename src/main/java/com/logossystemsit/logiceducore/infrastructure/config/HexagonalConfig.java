package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.membership.usecase.*;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.application.user.usecase.*;
import com.logossystemsit.logiceducore.domain.user.service.UserCreationPolicy;
import com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.adapter.EvaluationPeriodRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.repository.EvaluationPeriodJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.adapter.AcademicLevelRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.repository.AcademicLevelJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.adapter.AcademicPeriodRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.repository.AcademicPeriodJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.adapter.AcademicStructureRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.repository.AcademicStructureJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.adapter.MembershipRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.repository.MembershipJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.security.config.JwtProperties;
import com.logossystemsit.logiceducore.infrastructure.security.service.JwtService;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.adapter.UserRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.repository.UserJpaRepository;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class HexagonalConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public UserCreationPolicy userCreationPolicy() {
        return new UserCreationPolicy();
    }

    @Bean
    public JwtService jwtService(JwtProperties properties) {
        return new JwtService(properties);
    }

    // ---- Repository Adapters ----

    @Bean
    public UserRepository userRepositoryAdapter(UserJpaRepository jpa) {
        return new UserRepositoryAdapter(jpa);
    }

    @Bean
    public MembershipRepository membershipRepositoryAdapter(MembershipJpaRepository jpa) {
        return new MembershipRepositoryAdapter(jpa);
    }

    @Bean
    public AcademicStructureRepository academicStructureRepositoryAdapter(AcademicStructureJpaRepository jpa) {
        return new AcademicStructureRepositoryAdapter(jpa);
    }

    @Bean
    public AcademicLevelRepository academicLevelRepositoryAdapter(AcademicLevelJpaRepository jpa) {
        return new AcademicLevelRepositoryAdapter(jpa);
    }

    @Bean
    public AcademicPeriodRepository academicPeriodRepositoryAdapter(AcademicPeriodJpaRepository jpa) {
        return new AcademicPeriodRepositoryAdapter(jpa);
    }

    @Bean
    public EvaluationPeriodRepository evaluationPeriodRepositoryAdapter(EvaluationPeriodJpaRepository jpa) {
        return new EvaluationPeriodRepositoryAdapter(jpa);
    }

    // ---- User Use Cases ----

    @Bean
    public CreateUserService createUserService(
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            Clock clock,
            UserCreationPolicy userCreationPolicy) {
        return new CreateUserService(userRepository, membershipRepository, clock, userCreationPolicy);
    }

    @Bean
    public GetUserService getUserService(UserRepository userRepository) {
        return new GetUserService(userRepository);
    }

    @Bean
    public ListUsersService listUsersService(UserRepository userRepository) {
        return new ListUsersService(userRepository);
    }

    @Bean
    public AuthenticateUserService authenticateUserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new AuthenticateUserService(userRepository, passwordEncoder, jwtService);
    }

    @Bean
    public ChangePasswordService changePasswordService(
            UserRepository userRepository,
            Clock clock) {
        return new ChangePasswordService(userRepository, clock);
    }

    @Bean
    public ChangeUserStatusService changeUserStatusService(
            UserRepository userRepository,
            Clock clock) {
        return new ChangeUserStatusService(userRepository, clock);
    }

    // ---- Membership Use Cases ----

    @Bean
    public AssignMembershipService assignMembershipService(
            MembershipRepository membershipRepository) {
        return new AssignMembershipService(membershipRepository);
    }

    @Bean
    public ToggleMembershipService toggleMembershipService(
            MembershipRepository membershipRepository) {
        return new ToggleMembershipService(membershipRepository);
    }

    @Bean
    public ChangeMembershipScopeService changeMembershipScopeService(
            MembershipRepository membershipRepository) {
        return new ChangeMembershipScopeService(membershipRepository);
    }

    @Bean
    public ChangeMembershipRoleService changeMembershipRoleService(
            MembershipRepository membershipRepository) {
        return new ChangeMembershipRoleService(membershipRepository);
    }

    @Bean
    public GetUserMembershipsService getUserMembershipsService(
            MembershipRepository membershipRepository) {
        return new GetUserMembershipsService(membershipRepository);
    }
}
