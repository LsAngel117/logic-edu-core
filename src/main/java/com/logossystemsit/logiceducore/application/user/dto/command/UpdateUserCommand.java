package com.logossystemsit.logiceducore.application.user.dto.command;

import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;

import java.time.LocalDate;

public record UpdateUserCommand(
        UserId userId,
        Email email,
        Name name,
        User.Sex sex,
        LocalDate birthDate,
        Document document
) {}
