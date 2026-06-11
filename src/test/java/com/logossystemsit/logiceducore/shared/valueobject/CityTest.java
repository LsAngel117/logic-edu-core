package com.logossystemsit.logiceducore.shared.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CityTest {

    @Test
    void shouldCreateCityWithValidValue() {
        City city = new City("Medellín");

        assertThat(city.value()).isEqualTo("Medellín");
    }

    @Test
    void shouldRejectNullValue() {
        assertThatThrownBy(() -> new City(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    void shouldRejectBlankValue() {
        assertThatThrownBy(() -> new City("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    void shouldRejectEmptyValue() {
        assertThatThrownBy(() -> new City(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    void shouldRejectValueTooLong() {
        String tooLong = "A".repeat(101);
        assertThatThrownBy(() -> new City(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too long");
    }

    @Test
    void shouldAllowValueAtMaxLength() {
        String maxLength = "A".repeat(100);
        City city = new City(maxLength);

        assertThat(city.value()).isEqualTo(maxLength);
    }

}
