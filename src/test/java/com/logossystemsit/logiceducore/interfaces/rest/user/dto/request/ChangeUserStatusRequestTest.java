package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeUserStatusRequestTest {

    @Test
    void shouldCreateChangeUserStatusRequest() {
        ChangeUserStatusRequest request = new ChangeUserStatusRequest("ACTIVE");

        assertThat(request.status()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldCreateChangeUserStatusRequestWithInactiveStatus() {
        ChangeUserStatusRequest request = new ChangeUserStatusRequest("INACTIVE");

        assertThat(request.status()).isEqualTo("INACTIVE");
    }

    @Test
    void shouldCreateChangeUserStatusRequestWithBlockedStatus() {
        ChangeUserStatusRequest request = new ChangeUserStatusRequest("BLOCKED");

        assertThat(request.status()).isEqualTo("BLOCKED");
    }
}
