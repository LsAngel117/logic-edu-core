package com.logossystemsit.logiceducore.shared.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressTest {

    @Test
    void shouldCreateAddressWithValidValue() {
        Address address = Address.of("Calle 123 #45-67, Medellín");

        assertThat(address.value()).isEqualTo("Calle 123 #45-67, Medellín");
        assertThat(address.isPresent()).isTrue();
        assertThat(address.isEmpty()).isFalse();
    }

    @Test
    void shouldCreateEmptyAddress() {
        Address address = Address.empty();

        assertThat(address.value()).isNull();
        assertThat(address.isPresent()).isFalse();
        assertThat(address.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnEmptyForNullInput() {
        Address address = Address.of(null);

        assertThat(address.value()).isNull();
        assertThat(address.isPresent()).isFalse();
        assertThat(address.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnEmptyForBlankInput() {
        Address address = Address.of("   ");

        assertThat(address.value()).isNull();
        assertThat(address.isPresent()).isFalse();
        assertThat(address.isEmpty()).isTrue();
    }

    @Test
    void shouldNormalizeWhitespace() {
        Address address = Address.of("  Calle   123   #45-67,   Medellín  ");

        assertThat(address.value()).isEqualTo("Calle 123 #45-67, Medellín");
    }

    @Test
    void shouldRejectValueTooLong() {
        String tooLong = "A".repeat(256);
        assertThatThrownBy(() -> Address.of(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too long");
    }

    @Test
    void shouldAllowValueAtMaxLength() {
        String maxLength = "A".repeat(255);
        Address address = Address.of(maxLength);

        assertThat(address.value()).isEqualTo(maxLength);
    }

    @Test
    void equalsShouldWorkByValue() {
        Address a1 = Address.of("Calle 1");
        Address a2 = Address.of("Calle 1");
        Address a3 = Address.of("Calle 2");

        assertThat(a1).isEqualTo(a2);
        assertThat(a1).isNotEqualTo(a3);
    }

    @Test
    void emptyAddressesShouldBeEqual() {
        Address a1 = Address.empty();
        Address a2 = Address.of(null);

        assertThat(a1).isEqualTo(a2);
    }

    @Test
    void toStringShouldReturnEmptyStringForEmpty() {
        assertThat(Address.empty().toString()).isEqualTo("");
    }

    @Test
    void toStringShouldReturnValueForPresent() {
        assertThat(Address.of("Calle 1").toString()).isEqualTo("Calle 1");
    }
}
