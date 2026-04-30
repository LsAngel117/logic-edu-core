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

        int min = Math.min(3, firstGiven.length());
        for (int i = min; i <= firstGiven.length(); i++) {
            StringBuilder variant = new StringBuilder();

            variant.append(firstGiven, 0, i);

            if (secondGiven != null) {
                variant.append(secondGiven, 0, 1);
            }
            variant.append(firstFamily);

            if (secondFamily != null) {
                variant.append(secondFamily, 0, 1);
            }
            variants.add(variant.toString());
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
