package com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeMembershipScopeRequestTest {

    @Test
    void shouldCreateChangeMembershipScopeRequest() {
        ChangeMembershipScopeRequest request = new ChangeMembershipScopeRequest(
                "SCHOOL",
                "school-002"
        );

        assertThat(request.scopeType()).isEqualTo("SCHOOL");
        assertThat(request.scopeRefId()).isEqualTo("school-002");
    }

    @Test
    void shouldCreateChangeMembershipScopeRequestWithNullRefId() {
        ChangeMembershipScopeRequest request = new ChangeMembershipScopeRequest(
                "SCHOOL",
                null
        );

        assertThat(request.scopeType()).isEqualTo("SCHOOL");
        assertThat(request.scopeRefId()).isNull();
    }
}
