package com.logossystemsit.logiceducore.domain.user.service;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.Document;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class UserCreationPolicy {

    public void validate(Document document, LocalDate birthDate, LocalDate today) {
        Objects.requireNonNull(document, "Document is required");
        Objects.requireNonNull(birthDate, "Birth date is required");

        int age = Period.between(birthDate, today).getYears();

        switch (document.getType()) {
            case TI -> {
                if (age >= 18) {
                    throw new IllegalArgumentException("TI is only for minors");
                }
            }
            case CC -> {
                if (age < 18) {
                    throw new IllegalArgumentException("CC requires legal age");
                }
            }
        }
    }
}
