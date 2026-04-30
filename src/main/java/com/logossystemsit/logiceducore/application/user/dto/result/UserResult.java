package com.logossystemsit.logiceducore.application.user.dto.result;

import com.logossystemsit.logiceducore.domain.user.model.User;

import java.time.LocalDate;

public record UserResult(
        String id,
        String username,
        String email,
        String firstName,
        String lastName,
        String status,
        String sex,
        LocalDate birthDate
) {
    public static UserResult from(User user) {
        return new UserResult(
                user.getId().value(),
                user.getUsername().getValue(),
                user.getEmail().getValue(),
                user.getName().getFirstGivenName(),
                user.getName().getFirstFamilyName(),
                user.getStatus().name(),
                user.getSex().name(),
                user.getBirthDate()
        );
    }
}
