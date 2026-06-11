package com.logossystemsit.logiceducore.shared.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryTest {

    @Test
    void shouldCreateCountryWithValidValue() {
        Country country = new Country("Colombia");

        assertThat(country.value()).isEqualTo("Colombia");
    }

    @Test
    void shouldRejectNullValue() {
        assertThatThrownBy(() -> new Country(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    void shouldRejectBlankValue() {
        assertThatThrownBy(() -> new Country("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    void shouldRejectEmptyValue() {
        assertThatThrownBy(() -> new Country(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    void shouldRejectValueTooLong() {
        String tooLong = "A".repeat(101);
        assertThatThrownBy(() -> new Country(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too long");
    }

    @Test
    void shouldAllowValueAtMaxLength() {
        String maxLength = "A".repeat(100);
        Country country = new Country(maxLength);

        assertThat(country.value()).isEqualTo(maxLength);
    }

}
