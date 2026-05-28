package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.membership.usecase.*;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.application.user.usecase.*;
import com.logossystemsit.logiceducore.domain.user.service.UserCreationPolicy;
import com.logossystemsit.logiceducore.domain.user.service.UsernameBaseFormatter;
import com.logossystemsit.logiceducore.domain.user.service.UsernameDisambiguator;
import com.logossystemsit.logiceducore.domain.user.service.UsernameGenerator;
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

    // --- UserGenerator Configuration --- //
    @Bean
    public UsernameBaseFormatter usernameBaseFormatter() {
        return new UsernameBaseFormatter();
    }

    @Bean
    public UsernameDisambiguator usernameDisambiguator() {
        return new UsernameDisambiguator();
    }

    @Bean
    public UsernameGenerator usernameGenerator(
            UsernameBaseFormatter formatter,
            UsernameDisambiguator disambiguator
    ) {
        return new UsernameGenerator(formatter, disambiguator);
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

    // ---- User Use Cases ----

    @Bean
    public CreateUserService createUserService(
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            Clock clock,
            UserCreationPolicy userCreationPolicy,
            UsernameGenerator usernameGenerator) {
        return new CreateUserService(userRepository, membershipRepository, clock, userCreationPolicy, usernameGenerator);
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
