package com.logossystemsit.logiceducore.interfaces.rest.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipResponseTest {

    @Test
    void shouldCreateMembershipResponse() {
        MembershipResponse response = new MembershipResponse(
                "mem-001",
                "user-001",
                "STUDENT",
                "SCHOOL",
                "school-001",
                true
        );

        assertThat(response.id()).isEqualTo("mem-001");
        assertThat(response.userId()).isEqualTo("user-001");
        assertThat(response.role()).isEqualTo("STUDENT");
        assertThat(response.scopeType()).isEqualTo("SCHOOL");
        assertThat(response.scopeRefId()).isEqualTo("school-001");
        assertThat(response.active()).isTrue();
    }

    @Test
    void shouldCreateMembershipResponseWithInactiveStatus() {
        MembershipResponse response = new MembershipResponse(
                "mem-002",
                "user-002",
                "TEACHER",
                "SCHOOL",
                "school-001",
                false
        );

        assertThat(response.active()).isFalse();
        assertThat(response.role()).isEqualTo("TEACHER");
    }

    @Test
    void shouldCreateMembershipResponseWithNullScopeRefId() {
        MembershipResponse response = new MembershipResponse(
                "mem-003",
                "user-003",
                "ADMIN",
                "SCHOOL",
                null,
                true
        );

        assertThat(response.scopeRefId()).isNull();
    }
}
