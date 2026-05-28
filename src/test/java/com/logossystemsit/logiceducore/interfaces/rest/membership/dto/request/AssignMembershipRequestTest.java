package com.logossystemsit.logiceducore.interfaces.rest.membership.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssignMembershipRequestTest {

    @Test
    void shouldCreateAssignMembershipRequest() {
        AssignMembershipRequest request = new AssignMembershipRequest(
                "user-001",
                "STUDENT",
                "SCHOOL",
                "school-001"
        );

        assertThat(request.userId()).isEqualTo("user-001");
        assertThat(request.role()).isEqualTo("STUDENT");
        assertThat(request.scopeType()).isEqualTo("SCHOOL");
        assertThat(request.scopeRefId()).isEqualTo("school-001");
    }

    @Test
    void shouldCreateAssignMembershipRequestWithNullScopeRefId() {
        AssignMembershipRequest request = new AssignMembershipRequest(
                "user-001",
                "TEACHER",
                "SCHOOL",
                null
        );

        assertThat(request.scopeRefId()).isNull();
        assertThat(request.role()).isEqualTo("TEACHER");
    }
}
