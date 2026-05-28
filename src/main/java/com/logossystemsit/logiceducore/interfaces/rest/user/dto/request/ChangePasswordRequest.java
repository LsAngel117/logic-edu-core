package com.logossystemsit.logiceducore.interfaces.rest.user.dto.request;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
