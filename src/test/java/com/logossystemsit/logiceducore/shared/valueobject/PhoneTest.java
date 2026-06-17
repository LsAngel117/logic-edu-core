package com.logossystemsit.logiceducore.shared.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneTest {

    @Test
    void shouldCreatePhoneWithValidValue() {
        Phone phone = Phone.of("+57 300 123 4567");

        assertThat(phone.value()).isEqualTo("+57 300 123 4567");
        assertThat(phone.isPresent()).isTrue();
        assertThat(phone.isEmpty()).isFalse();
    }

    @Test
    void shouldCreateEmptyPhone() {
        Phone phone = Phone.empty();

        assertThat(phone.value()).isNull();
        assertThat(phone.isPresent()).isFalse();
        assertThat(phone.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnEmptyForNullInput() {
        Phone phone = Phone.of(null);

        assertThat(phone.value()).isNull();
        assertThat(phone.isPresent()).isFalse();
        assertThat(phone.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnEmptyForBlankInput() {
        Phone phone = Phone.of("   ");

        assertThat(phone.value()).isNull();
        assertThat(phone.isPresent()).isFalse();
        assertThat(phone.isEmpty()).isTrue();
    }

    @Test
    void shouldNormalizeWhitespace() {
        Phone phone = Phone.of("  +57   300  123  4567  ");

        assertThat(phone.value()).isEqualTo("+57 300 123 4567");
    }

    @Test
    void shouldRejectValueTooLong() {
        String tooLong = "+".repeat(21);
        assertThatThrownBy(() -> Phone.of(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too long");
    }

    @Test
    void shouldAllowValueAtMaxLength() {
        String maxLength = "1".repeat(20);
        Phone phone = Phone.of(maxLength);

        assertThat(phone.value()).isEqualTo(maxLength);
    }

    @Test
    void equalsShouldWorkByValue() {
        Phone p1 = Phone.of("123");
        Phone p2 = Phone.of("123");
        Phone p3 = Phone.of("456");

        assertThat(p1).isEqualTo(p2);
        assertThat(p1).isNotEqualTo(p3);
    }

    @Test
    void emptyPhonesShouldBeEqual() {
        Phone p1 = Phone.empty();
        Phone p2 = Phone.of(null);

        assertThat(p1).isEqualTo(p2);
    }

    @Test
    void toStringShouldReturnEmptyStringForEmpty() {
        assertThat(Phone.empty().toString()).isEqualTo("");
    }

    @Test
    void toStringShouldReturnValueForPresent() {
        assertThat(Phone.of("123").toString()).isEqualTo("123");
    }
}
