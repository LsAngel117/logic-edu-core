package com.logossystemsit.logiceducore.application.user.dto.command;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.*;

import java.time.LocalDate;

public record CreateUserCommand(
        UserId userId,
        Username username,
        Email email,
        PasswordHash passwordHash,
        Name name,
        User.Sex sex,
        LocalDate birthDate,
        Document document,

        // clave: membership
        Role role,
        Scope scope
) {}