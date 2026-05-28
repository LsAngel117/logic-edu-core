package com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeMembershipRoleRequestTest {

    @Test
    void shouldCreateChangeMembershipRoleRequest() {
        ChangeMembershipRoleRequest request = new ChangeMembershipRoleRequest("TEACHER");

        assertThat(request.role()).isEqualTo("TEACHER");
    }

    @Test
    void shouldCreateChangeMembershipRoleRequestWithDifferentRole() {
        ChangeMembershipRoleRequest request = new ChangeMembershipRoleRequest("ADMIN");

        assertThat(request.role()).isEqualTo("ADMIN");
    }
}
