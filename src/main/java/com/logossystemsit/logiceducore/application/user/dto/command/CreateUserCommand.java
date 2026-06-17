package com.logossystemsit.logiceducore.application.user.dto.command;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.*;
import com.logossystemsit.logiceducore.shared.valueobject.*;

import java.time.LocalDate;

public record CreateUserCommand(
        UserId userId,
        Email email,
        PasswordHash passwordHash,
        Name name,
        User.Sex sex,
        LocalDate birthDate,
        Document document,
        Phone phone,
        Address address,
        City city,
        Country country,
        Role role,
        Scope scope
) {}