package com.logossystemsit.logiceducore.domain.user.model;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.valueobject.*;

import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    private static final Instant NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final Instant PAST = Instant.parse("2024-06-15T12:00:00Z");
    private static final Instant EARLIER = Instant.parse("2023-06-15T12:00:00Z");

    private static UserId userId() { return new UserId("123e4567-e89b-12d3-a456-426614174000"); }
    private static Username username() { return new Username("jdoe123"); }
    private static Email email() { return new Email("jdoe@example.com"); }
    private static PasswordHash passwordHash() {
        return new PasswordHash("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab");
    }
    private static PasswordHash otherPasswordHash() {
        return new PasswordHash("$2a$10$zyxwvutsrqponmlkjihgfedcbaABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab");
    }
    private static Name name() { return new Name("John", null, "Doe", null); }
    private static Document ccDocument() {
        return new Document(Document.DocumentType.CC, new DocumentNumber("1234567890"));
    }
    private static LocalDate validBirthDate() { return LocalDate.of(1990, 1, 15); }
    private static Phone phone() { return Phone.of("+57 300 123 4567"); }
    private static Address address() { return Address.of("Calle 123 #45-67"); }
    private static City city() { return new City("Medellín"); }
    private static Country country() { return new Country("Colombia"); }

    // ==================== create() factory ====================

    @Nested
    class CreateFactory {

        @Test
        void shouldCreateActiveUserWithGivenData() {
            User user = User.create(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(), null, null, null, null, NOW);

            assertThat(user.getId()).isEqualTo(userId());
            assertThat(user.getUsername()).isEqualTo(username());
            assertThat(user.getEmail()).isEqualTo(email());
            assertThat(user.getPasswordHash()).isEqualTo(passwordHash());
            assertThat(user.getName()).isEqualTo(name());
            assertThat(user.getStatus()).isEqualTo(User.Status.ACTIVE);
            assertThat(user.getCreatedAt()).isEqualTo(NOW);
            assertThat(user.getUpdatedAt()).isEqualTo(NOW);
            assertThat(user.isActive()).isTrue();
        }

        @Test
        void shouldRejectFutureBirthDate() {
            LocalDate futureDate = NOW.atZone(ZoneOffset.UTC).toLocalDate().plusDays(1);

            assertThatThrownBy(() -> User.create(userId(), username(), email(), passwordHash(),
                    name(), User.Sex.MALE, futureDate, ccDocument(), null, null, null, null, NOW))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Birth date cannot be in the future");
        }

        @Test
        void shouldRejectNullId() {
            assertThatThrownBy(() -> User.create(null, username(), email(), passwordHash(),
                    name(), User.Sex.MALE, validBirthDate(), ccDocument(), null, null, null, null, NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("UserId");
        }

        @Test
        void shouldRejectNullUsername() {
            assertThatThrownBy(() -> User.create(userId(), null, email(), passwordHash(),
                    name(), User.Sex.MALE, validBirthDate(), ccDocument(), null, null, null, null, NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Username");
        }

        @Test
        void shouldRejectNullEmail() {
            assertThatThrownBy(() -> User.create(userId(), username(), null, passwordHash(),
                    name(), User.Sex.MALE, validBirthDate(), ccDocument(), null, null, null, null, NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Email");
        }
    }

    // ==================== restore() factory ====================

    @Nested
    class RestoreFactory {

        @Test
        void shouldRestoreUserFromPersistence() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.INACTIVE, PAST, NOW);

            assertThat(user.getStatus()).isEqualTo(User.Status.INACTIVE);
            assertThat(user.getCreatedAt()).isEqualTo(PAST);
            assertThat(user.getUpdatedAt()).isEqualTo(NOW);
            assertThat(user.isActive()).isFalse();
        }

        @Test
        void shouldRejectInvalidTimestampsCreatedAtAfterUpdatedAt() {
            assertThatThrownBy(() -> User.restore(userId(), username(), email(), passwordHash(),
                    name(), User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, NOW, PAST))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Invalid timestamps");
        }

        @Test
        void shouldRejectNullCreatedAt() {
            assertThatThrownBy(() -> User.restore(userId(), username(), email(), passwordHash(),
                    name(), User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, null, NOW))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void shouldRejectNullUpdatedAt() {
            assertThatThrownBy(() -> User.restore(userId(), username(), email(), passwordHash(),
                    name(), User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    // ==================== activate() ====================

    @Nested
    class Activate {

        @Test
        void shouldActivateInactiveUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.INACTIVE, PAST, PAST);

            User activated = user.activate(NOW);

            assertThat(activated.getStatus()).isEqualTo(User.Status.ACTIVE);
            assertThat(activated.getUpdatedAt()).isEqualTo(NOW);
            assertThat(activated.isActive()).isTrue();
        }

        @Test
        void shouldRejectActivateAlreadyActiveUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, PAST);

            assertThatThrownBy(() -> user.activate(NOW))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("User already active");
        }

        @Test
        void shouldActivateBlockedUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.BLOCKED, PAST, PAST);

            User activated = user.activate(NOW);

            assertThat(activated.getStatus()).isEqualTo(User.Status.ACTIVE);
            assertThat(activated.isActive()).isTrue();
        }

        @Test
        void shouldRejectActivateWithTimestampBeforeCurrent() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.INACTIVE, PAST, NOW);

            assertThatThrownBy(() -> user.activate(PAST))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Invalid time progression");
        }
    }

    // ==================== deactivate() ====================

    @Nested
    class Deactivate {

        @Test
        void shouldDeactivateActiveUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, PAST);

            User deactivated = user.deactivate(NOW);

            assertThat(deactivated.getStatus()).isEqualTo(User.Status.INACTIVE);
            assertThat(deactivated.getUpdatedAt()).isEqualTo(NOW);
            assertThat(deactivated.isActive()).isFalse();
        }

        @Test
        void shouldRejectDeactivateAlreadyInactiveUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.INACTIVE, PAST, PAST);

            assertThatThrownBy(() -> user.deactivate(NOW))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("User already inactive");
        }
    }

    // ==================== block() ====================

    @Nested
    class Block {

        @Test
        void shouldBlockActiveUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, PAST);

            User blocked = user.block(NOW);

            assertThat(blocked.getStatus()).isEqualTo(User.Status.BLOCKED);
            assertThat(blocked.getUpdatedAt()).isEqualTo(NOW);
        }

        @Test
        void shouldBlockInactiveUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.INACTIVE, PAST, PAST);

            User blocked = user.block(NOW);

            assertThat(blocked.getStatus()).isEqualTo(User.Status.BLOCKED);
        }

        @Test
        void shouldRejectBlockAlreadyBlockedUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.BLOCKED, PAST, PAST);

            assertThatThrownBy(() -> user.block(NOW))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("User already blocked");
        }
    }

    // ==================== changePassword() ====================

    @Nested
    class ChangePassword {

        @Test
        void shouldChangePasswordSuccessfully() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, PAST);

            User updated = user.changePassword(otherPasswordHash(), NOW);

            assertThat(updated.getPasswordHash()).isEqualTo(otherPasswordHash());
            assertThat(updated.getUpdatedAt()).isEqualTo(NOW);
        }

        @Test
        void shouldRejectSamePassword() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, PAST);

            assertThatThrownBy(() -> user.changePassword(passwordHash(), NOW))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("New password cannot be the same as the current one");
        }

        @Test
        void shouldRejectPasswordChangeOnBlockedUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.BLOCKED, PAST, PAST);

            assertThatThrownBy(() -> user.changePassword(otherPasswordHash(), NOW))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Blocked user cannot change password");
        }

        @Test
        void shouldRejectNullPassword() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, PAST);

            assertThatThrownBy(() -> user.changePassword(null, NOW))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("New password");
        }
    }

    // ==================== isActive() ====================

    @Nested
    class IsActive {

        @Test
        void shouldReturnTrueForActiveUser() {
            User user = User.create(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(), null, null, null, null, NOW);

            assertThat(user.isActive()).isTrue();
        }

        @Test
        void shouldReturnFalseForInactiveUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.INACTIVE, PAST, NOW);

            assertThat(user.isActive()).isFalse();
        }

        @Test
        void shouldReturnFalseForBlockedUser() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.BLOCKED, PAST, NOW);

            assertThat(user.isActive()).isFalse();
        }
    }

    // ==================== VO validation ====================

    @Nested
    class ValueObjectValidation {

        @Test
        void emailShouldBeNormalizedToLowerCase() {
            Email email = new Email("JDOE@EXAMPLE.COM");

            assertThat(email.getValue()).isEqualTo("jdoe@example.com");
        }

        @Test
        void emailShouldRejectInvalidFormat() {
            assertThatThrownBy(() -> new Email("not-an-email"))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Invalid email format");
        }

        @Test
        void usernameShouldBeNormalizedToLowerCase() {
            Username uname = new Username("JDOE123");

            assertThat(uname.getValue()).isEqualTo("jdoe123");
        }

        @Test
        void usernameShouldRejectTooShort() {
            assertThatThrownBy(() -> new Username("ab"))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Invalid username format");
        }

        @Test
        void usernameShouldRejectStartingWithNumber() {
            assertThatThrownBy(() -> new Username("1jdoe123"))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessage("Invalid username format");
        }

        @Test
        void documentCCShouldAccept6To10Digits() {
            Document doc = new Document(Document.DocumentType.CC, new DocumentNumber("1234567890"));
            assertThat(doc.getType()).isEqualTo(Document.DocumentType.CC);
            assertThat(doc.numberValue()).isEqualTo("1234567890");
        }

        @Test
        void documentCCShouldRejectInvalidFormat() {
            assertThatThrownBy(() -> new Document(Document.DocumentType.CC, new DocumentNumber("abc")))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Invalid CC format");
        }

        @Test
        void documentTIShouldAccept6To10Digits() {
            Document doc = new Document(Document.DocumentType.TI, new DocumentNumber("9876543210"));
            assertThat(doc.getType()).isEqualTo(Document.DocumentType.TI);
            assertThat(doc.numberValue()).isEqualTo("9876543210");
        }

        @Test
        void documentTIShouldRejectInvalidFormat() {
            assertThatThrownBy(() -> new Document(Document.DocumentType.TI, new DocumentNumber("abc")))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasMessageContaining("Invalid TI format");
        }

        @Test
        void nameShouldCapitalizeWords() {
            Name n = new Name("john", null, "doe", null);

            assertThat(n.getFirstGivenName()).isEqualTo("John");
            assertThat(n.getFirstFamilyName()).isEqualTo("Doe");
        }

        @Test
        void nameShouldHandleParticles() {
            Name n = new Name("maria", null, "de la rosa", null);

            assertThat(n.getFirstGivenName()).isEqualTo("Maria");
            assertThat(n.getFirstFamilyName()).isEqualTo("De la Rosa");
        }
    }

    // ==================== Contact Info ====================

    @Nested
    class ContactInfo {

        @Test
        void shouldRestoreUserWithContactInfo() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    phone(), address(), city(), country(),
                    User.Status.ACTIVE, PAST, NOW);

            assertThat(user.getPhone()).isEqualTo(phone());
            assertThat(user.getAddress()).isEqualTo(address());
            assertThat(user.getCity()).isEqualTo(city());
            assertThat(user.getCountry()).isEqualTo(country());
        }

        @Test
        void shouldRestoreUserWithoutContactInfo() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, NOW);

            assertThat(user.getPhone()).isNull();
            assertThat(user.getAddress()).isNull();
            assertThat(user.getCity()).isNull();
            assertThat(user.getCountry()).isNull();
        }

        @Test
        void createShouldSetContactInfoToNull() {
            User user = User.create(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(), null, null, null, null, NOW);

            assertThat(user.getPhone()).isNull();
            assertThat(user.getAddress()).isNull();
            assertThat(user.getCity()).isNull();
            assertThat(user.getCountry()).isNull();
        }

        @Test
        void changeBasicInfoShouldUpdateContactInfo() {
            User user = User.restore(userId(), username(), email(), passwordHash(), name(),
                    User.Sex.MALE, validBirthDate(), ccDocument(),
                    null, null, null, null,
                    User.Status.ACTIVE, PAST, PAST);

            User updated = user.changeBasicInfo(email(), name(), User.Sex.MALE,
                    validBirthDate(), ccDocument(), NOW,
                    phone(), address(), city(), country());

            assertThat(updated.getPhone()).isEqualTo(phone());
            assertThat(updated.getAddress()).isEqualTo(address());
            assertThat(updated.getCity()).isEqualTo(city());
            assertThat(updated.getCountry()).isEqualTo(country());
        }
    }
}
