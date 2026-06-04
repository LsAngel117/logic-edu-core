package com.logossystemsit.logiceducore.application.user.usecase;
import com.logossystemsit.logiceducore.shared.errors.exceptions.BusinessRuleException;
import com.logossystemsit.logiceducore.shared.errors.exceptions.ResourceNotFoundException;

import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.user.dto.command.CreateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.CreateUserResult;
import com.logossystemsit.logiceducore.application.user.port.in.CreateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.membership.model.Membership;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.domain.user.service.UserCreationPolicy;
import com.logossystemsit.logiceducore.domain.user.service.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private Clock clock;

    @Mock
    private UsernameGenerator usernameGenerator;

    private final UserCreationPolicy policy = new UserCreationPolicy();

    private CreateUserUseCase useCase;

    private static final Instant FIXED_NOW = Instant.parse("2025-06-15T12:00:00Z");
    private static final UserId USER_ID = new UserId("123e4567-e89b-12d3-a456-426614174000");
    private static final Email EMAIL = new Email("jdoe@example.com");
    private static final PasswordHash PASSWORD = new PasswordHash(
            "$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ab");
    private static final Name NAME = new Name("John", null, "Doe", null);
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 15);
    private static final Role ROLE = Role.STUDENT;
    private static final Scope SCOPE = Scope.course("course-123");

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_NOW);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(usernameGenerator.generate(NAME)).thenReturn(List.of("jdoe", "johndoe", "jdoe1"));
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        useCase = new CreateUserService(userRepository, membershipRepository, clock, policy, usernameGenerator);
    }

    @Test
    void execute_shouldCreateUserAndMembership() {
        CreateUserCommand command = new CreateUserCommand(USER_ID, EMAIL, PASSWORD,
                NAME, User.Sex.MALE, BIRTH_DATE,
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                ROLE, SCOPE);

        CreateUserResult result = useCase.execute(command);

        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.username()).isEqualTo("jdoe");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getStatus()).isEqualTo(User.Status.ACTIVE);
        assertThat(savedUser.isActive()).isTrue();

        verify(membershipRepository).save(any(Membership.class));
    }

    @Test
    void execute_shouldSetCorrectTimestamps() {
        CreateUserCommand command = new CreateUserCommand(USER_ID, EMAIL, PASSWORD,
                NAME, User.Sex.MALE, BIRTH_DATE,
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                ROLE, SCOPE);

        useCase.execute(command);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getCreatedAt()).isEqualTo(FIXED_NOW);
        assertThat(saved.getUpdatedAt()).isEqualTo(FIXED_NOW);
    }

    @Test
    void execute_shouldRejectCCForMinor() {
        LocalDate minorBirthDate = LocalDate.of(2015, 1, 15);
        CreateUserCommand command = new CreateUserCommand(USER_ID, EMAIL, PASSWORD,
                NAME, User.Sex.MALE, minorBirthDate,
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                ROLE, SCOPE);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("CC requires legal age");
    }

    @Test
    void execute_shouldRejectTIForAdult() {
        LocalDate adultBirthDate = LocalDate.of(1990, 1, 15);
        CreateUserCommand command = new CreateUserCommand(USER_ID, EMAIL, PASSWORD,
                NAME, User.Sex.MALE, adultBirthDate,
                new Document(Document.DocumentType.TI, new DocumentNumber("1234567890")),
                ROLE, SCOPE);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("TI is only for minors");
    }
}
