package com.logossystemsit.logiceducore.application.user.dto.result;

import com.logossystemsit.logiceducore.domain.user.model.User;

import java.time.LocalDate;

public record UserResult(
        String id,
        String username,
        String email,
        String firstName,
        String secondName,
        String lastName,
        String secondLastName,
        String status,
        String sex,
        LocalDate birthDate,
        String documentType,
        String documentValue,
        String createdAt,
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
                user.getName().getSecondGivenName(),
                user.getName().getFirstFamilyName(),
                user.getName().getSecondFamilyName(),
                user.getStatus().name(),
                user.getSex().name(),
                user.getBirthDate(),
                user.getDocument().typeValue(),
                user.getDocument().numberValue(),
                user.getCreatedAt().toString(),
                user.getPhone() != null ? user.getPhone().value() : null,
                user.getAddress() != null ? user.getAddress().value() : null,
                user.getCity() != null ? user.getCity().value() : null,
                user.getCountry() != null ? user.getCountry().value() : null
        );
    }
}
