package com.logossystemsit.logiceducore.domain.user.service;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.Name;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

public class UsernameBaseFormatter {

    private static final Set<String> PARTICLES = Set.of(
            "de", "del", "la", "las", "los", "da", "dos", "do"
    );

    public String format(Name name) {

        String firstGiven = cleanGivenName(name.getFirstGivenName());
        String secondGiven = cleanGivenNameOptional(name.getSecondGivenName());

        String firstFamily = cleanFamilyName(name.getFirstFamilyName());
        String secondFamily = cleanFamilyNameOptional(name.getSecondFamilyName());

        StringBuilder username = new StringBuilder();

        username.append(firstLetters(firstGiven, 2));

        if (secondGiven != null && !secondGiven.isBlank()) {
            username.append(firstLetters(secondGiven, 1));
        }

        username.append(firstFamily);

        if (secondFamily != null && !secondFamily.isBlank()) {
            username.append(firstLetters(secondFamily, 1));
        }

        return username.toString();
    }

    private String normalizeWithSpaces(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        normalized = normalized.replace("ñ", "n").replace("Ñ", "n");

        return normalized
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String cleanGivenName(String value) {
        String normalized = normalizeWithSpaces(value);
        return normalized.replaceAll("[^a-z]", "");
    }

    private String cleanFamilyName(String value) {
        String normalized = normalizeWithSpaces(value);

        String[] parts = normalized.split(" ");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!PARTICLES.contains(part)) {
                result.append(part);
            }
        }

        if (result.isEmpty()) {
            return normalized.replace(" ", "");
        }

        return result.toString();
    }

    private String cleanGivenNameOptional(String value) {
        if (value == null || value.isBlank()) return null;
        return cleanGivenName(value);
    }

    private String cleanFamilyNameOptional(String value) {
        if (value == null || value.isBlank()) return null;
        return cleanFamilyName(value);
    }

    private String firstLetters(String value, int count) {
        if (value == null || value.isEmpty()) return "";
        return value.substring(0, Math.min(count, value.length()));
    }
}
