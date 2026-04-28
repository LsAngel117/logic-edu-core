package com.logossystemsit.logiceducore.domain.user.service;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.Name;

import java.util.ArrayList;
import java.util.List;

public class UsernameDisambiguator {

    public List<String> generateVariants(Name name, String baseUsername) {
        List<String> variants = new ArrayList<>();
        variants.add(baseUsername);

        String firstGiven = normalize(name.getFirstGivenName());
        String secondGiven = normalizeOptional(name.getSecondGivenName());
        String firstFamily = normalize(name.getFirstFamilyName());
        String secondFamily = normalizeOptional(name.getSecondFamilyName());

        for (int i = 3; i <= firstGiven.length(); i++) {
            String variant = firstGiven.substring(0, i);

            if (secondGiven != null && !secondGiven.isBlank()) {
                variant += secondGiven.substring(0, Math.min(1, secondGiven.length()));
            }

            variant += firstFamily;

            if (secondFamily != null && !secondFamily.isBlank()) {
                variant += secondFamily.substring(0, 1);
            }

            variants.add(variant);
        }

        return variants.stream().distinct().toList();
    }

    private String normalize(String value) {
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("ñ", "n")
                .replace("Ñ", "n")
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z]", "");
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) return null;
        return normalize(value);
    }
}
