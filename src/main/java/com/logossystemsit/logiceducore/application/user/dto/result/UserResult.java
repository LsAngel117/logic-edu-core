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
        LocalDate birthDate,
        String phone,
        String address,
        String city,
        String country
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
                user.getBirthDate(),
                user.getPhone() != null ? user.getPhone().value() : null,
                user.getAddress() != null ? user.getAddress().value() : null,
                user.getCity() != null ? user.getCity().value() : null,
                user.getCountry() != null ? user.getCountry().value() : null
        );
    }
}
