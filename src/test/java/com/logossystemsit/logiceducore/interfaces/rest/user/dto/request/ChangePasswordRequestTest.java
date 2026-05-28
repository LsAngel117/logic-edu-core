package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordRequestTest {

    @Test
    void shouldCreateChangePasswordRequest() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword");

        assertThat(request.currentPassword()).isEqualTo("oldPassword");
        assertThat(request.newPassword()).isEqualTo("newPassword");
    }

    @Test
    void shouldCreateChangePasswordRequestWithEmptyPasswords() {
        ChangePasswordRequest request = new ChangePasswordRequest("", "");

        assertThat(request.currentPassword()).isEmpty();
        assertThat(request.newPassword()).isEmpty();
    }
}
