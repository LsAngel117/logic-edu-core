package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.membership.usecase.*;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.application.user.usecase.*;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.adapter.MembershipRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.repository.MembershipJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.security.config.JwtProperties;
import com.logossystemsit.logiceducore.infrastructure.security.service.JwtService;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.adapter.UserRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.repository.UserJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HexagonalConfigTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private MembershipJpaRepository membershipJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtProperties jwtProperties;

    private HexagonalConfig config;

    @BeforeEach
    void setUp() {
        config = new HexagonalConfig();
        jwtProperties = new JwtProperties(
                "test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm!!",
                7200000L
        );
    }

    @Test
    void clock_shouldReturnSystemUTC() {
        Clock clock = config.clock();

        assertThat(clock.getZone()).isEqualTo(ZoneOffset.UTC);
    }

    @Test
    void userRepositoryAdapter_shouldReturnUserRepositoryAdapter() {
        UserRepository adapter = config.userRepositoryAdapter(userJpaRepository);

        assertThat(adapter).isInstanceOf(UserRepositoryAdapter.class);
    }

    @Test
    void membershipRepositoryAdapter_shouldReturnMembershipRepositoryAdapter() {
        MembershipRepository adapter = config.membershipRepositoryAdapter(membershipJpaRepository);

        assertThat(adapter).isInstanceOf(MembershipRepositoryAdapter.class);
    }

    @Test
    void jwtService_shouldReturnJwtService() {
        JwtService service = config.jwtService(jwtProperties);

        assertThat(service).isNotNull();
    }

    @Test
    void createUserService_shouldReturnCreateUserService() {
        UserRepository userRepo = config.userRepositoryAdapter(userJpaRepository);
        MembershipRepository membershipRepo = config.membershipRepositoryAdapter(membershipJpaRepository);
        Clock clock = config.clock();

        var service = config.createUserService(userRepo, membershipRepo, clock, config.userCreationPolicy());

        assertThat(service).isInstanceOf(CreateUserService.class);
    }

    @Test
    void getUserService_shouldReturnGetUserService() {
        UserRepository userRepo = config.userRepositoryAdapter(userJpaRepository);

        var service = config.getUserService(userRepo);

        assertThat(service).isInstanceOf(GetUserService.class);
    }

    @Test
    void listUsersService_shouldReturnListUsersService() {
        UserRepository userRepo = config.userRepositoryAdapter(userJpaRepository);

        var service = config.listUsersService(userRepo);

        assertThat(service).isInstanceOf(ListUsersService.class);
    }

    @Test
    void authenticateUserService_shouldReturnAuthenticateUserService() {
        UserRepository userRepo = config.userRepositoryAdapter(userJpaRepository);
        JwtService jwtService = config.jwtService(jwtProperties);

        var service = config.authenticateUserService(userRepo, passwordEncoder, jwtService);

        assertThat(service).isInstanceOf(AuthenticateUserService.class);
    }

    @Test
    void changePasswordService_shouldReturnChangePasswordService() {
        UserRepository userRepo = config.userRepositoryAdapter(userJpaRepository);
        Clock clock = config.clock();

        var service = config.changePasswordService(userRepo, clock);

        assertThat(service).isInstanceOf(ChangePasswordService.class);
    }

    @Test
    void changeUserStatusService_shouldReturnChangeUserStatusService() {
        UserRepository userRepo = config.userRepositoryAdapter(userJpaRepository);
        Clock clock = config.clock();

        var service = config.changeUserStatusService(userRepo, clock);

        assertThat(service).isInstanceOf(ChangeUserStatusService.class);
    }

    @Test
    void assignMembershipService_shouldReturnAssignMembershipService() {
        MembershipRepository membershipRepo = config.membershipRepositoryAdapter(membershipJpaRepository);

        var service = config.assignMembershipService(membershipRepo);

        assertThat(service).isInstanceOf(AssignMembershipService.class);
    }

    @Test
    void toggleMembershipService_shouldReturnToggleMembershipService() {
        MembershipRepository membershipRepo = config.membershipRepositoryAdapter(membershipJpaRepository);

        var service = config.toggleMembershipService(membershipRepo);

        assertThat(service).isInstanceOf(ToggleMembershipService.class);
    }

    @Test
    void changeMembershipScopeService_shouldReturnChangeMembershipScopeService() {
        MembershipRepository membershipRepo = config.membershipRepositoryAdapter(membershipJpaRepository);

        var service = config.changeMembershipScopeService(membershipRepo);

        assertThat(service).isInstanceOf(ChangeMembershipScopeService.class);
    }

    @Test
    void changeMembershipRoleService_shouldReturnChangeMembershipRoleService() {
        MembershipRepository membershipRepo = config.membershipRepositoryAdapter(membershipJpaRepository);

        var service = config.changeMembershipRoleService(membershipRepo);

        assertThat(service).isInstanceOf(ChangeMembershipRoleService.class);
    }

    @Test
    void getUserMembershipsService_shouldReturnGetUserMembershipsService() {
        MembershipRepository membershipRepo = config.membershipRepositoryAdapter(membershipJpaRepository);

        var service = config.getUserMembershipsService(membershipRepo);

        assertThat(service).isInstanceOf(GetUserMembershipsService.class);
    }
}
