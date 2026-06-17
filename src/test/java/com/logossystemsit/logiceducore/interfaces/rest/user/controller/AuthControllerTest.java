package com.logossystemsit.logiceducore.interfaces.rest.user.controller;

import com.logossystemsit.logiceducore.application.user.dto.command.CreateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.command.LoginCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.CreateUserResult;
import com.logossystemsit.logiceducore.application.user.dto.result.LoginResult;
import com.logossystemsit.logiceducore.application.user.port.in.AuthenticateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.in.CreateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.infrastructure.security.service.JwtService;
import com.logossystemsit.logiceducore.interfaces.rest.user.dto.request.LoginRequest;
import com.logossystemsit.logiceducore.interfaces.rest.user.dto.request.RegisterRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice() // enables @ControllerAdvice if any
                .build();
    }

    @Test
    void registerWithValidDataShouldReturn201WithAuthResponse() throws Exception {
        RegisterRequest request = validRegisterRequest();
        CreateUserResult result = new CreateUserResult(
                UserId.generate(),
                "johnsmith"
        );

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        when(createUserUseCase.execute(any(CreateUserCommand.class))).thenReturn(result);
        when(jwtService.generate(any(), any())).thenReturn("test-jwt-token");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.userId").value(result.userId().value()))
                .andExpect(jsonPath("$.username").value("johnsmith"));
    }

    @Test
    void registerWithDuplicateEmailShouldReturn409() throws Exception {
        RegisterRequest request = validRegisterRequest();

        when(userRepository.existsByEmail(any())).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerWithDuplicateUsernameShouldReturn409() throws Exception {
        RegisterRequest request = validRegisterRequest();

        when(userRepository.existsByUsername(any())).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginWithValidCredentialsShouldReturn200WithAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "password123");
        UserId userId = UserId.generate();
        LoginResult result = new LoginResult("test-jwt-token", userId, "johnsmith");
        User user = mockUser(userId);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(authenticateUserUseCase.execute(any(LoginCommand.class))).thenReturn(result);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.userId").value(userId.value()))
                .andExpect(jsonPath("$.username").value("johnsmith"));
    }

    @Test
    void loginWithUnknownUsernameShouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest("unknown@example.com", "password123");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithInvalidPasswordShouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "wrongpassword");
        UserId userId = UserId.generate();
        User user = mockUser(userId);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(authenticateUserUseCase.execute(any(LoginCommand.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private static RegisterRequest validRegisterRequest() {
        return new RegisterRequest(
                "johnsmith",
                "john@example.com",
                "password123",
                "John",
                null,
                "Smith",
                null,
                "MALE",
                "2000-01-01",
                "CC",
                "1234567890",
                "STUDENT",
                "COURSE",
                "course-123"
        );
    }

    private static User mockUser(UserId userId) {
        return User.restore(
                userId,
                new Username("johnsmith"),
                new Email("john@example.com"),
                new PasswordHash("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"),
                new Name("John", null, "Smith", null),
                User.Sex.MALE,
                LocalDate.of(2000, 1, 1),
                new Document(Document.DocumentType.CC, new DocumentNumber("1234567890")),
                null, null, null, null,
                User.Status.ACTIVE,
                Instant.now(),
                Instant.now()
        );
    }
}
