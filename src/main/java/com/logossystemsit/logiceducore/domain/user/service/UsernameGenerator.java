package com.logossystemsit.logiceducore.domain.user.service;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.Name;

import java.util.List;

public class UsernameGenerator {

    private final UsernameBaseFormatter formatter;
    private final UsernameDisambiguator disambiguator;

    public UsernameGenerator(UsernameBaseFormatter formatter,
                             UsernameDisambiguator disambiguator) {
        this.formatter = formatter;
        this.disambiguator = disambiguator;
    }

    public List<String> generate(Name name) {
        String base = formatter.format(name);
        return disambiguator.generateVariants(name, base);
    }
}
