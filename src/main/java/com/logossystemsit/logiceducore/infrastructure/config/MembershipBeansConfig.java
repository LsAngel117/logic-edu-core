package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.membership.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MembershipBeansConfig {

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
