package com.logossystemsit.logiceducore.interfaces.rest.user.controller;

import com.logossystemsit.logiceducore.application.user.dto.command.ChangePasswordCommand;
import com.logossystemsit.logiceducore.application.user.dto.command.ChangeUserStatusCommand;
import com.logossystemsit.logiceducore.application.user.dto.command.CreateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.CreateUserResult;
import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.application.user.port.in.*;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.interfaces.rest.user.dto.request.*;

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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private GetUserUseCase getUserUseCase;

    @Mock
    private ListUsersUseCase listUsersUseCase;

    @Mock
    private ChangeUserStatusUseCase changeUserStatusUseCase;

    @Mock
    private ChangePasswordUseCase changePasswordUseCase;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createUserShouldReturn201WithUserResponse() throws Exception {
        CreateUserRequest request = validCreateUserRequest();
        UserId userId = UserId.generate();
        CreateUserResult createResult = new CreateUserResult(userId, "johnsmith");
        UserResult userResult = new UserResult(
                userId.value(), "johnsmith", "john@example.com",
                "John", "Smith", "ACTIVE", "MALE", LocalDate.of(2000, 1, 1)
        );

        when(passwordEncoder.encode(any())).thenReturn("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        when(createUserUseCase.execute(any(CreateUserCommand.class))).thenReturn(createResult);
        when(getUserUseCase.execute(any(UserId.class))).thenReturn(userResult);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.value()))
                .andExpect(jsonPath("$.username").value("johnsmith"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserShouldReturn200WithUserResponse() throws Exception {
        UserId userId = UserId.generate();
        UserResult result = new UserResult(
                userId.value(), "johnsmith", "john@example.com",
                "John", "Smith", "ACTIVE", "MALE", LocalDate.of(2000, 1, 1)
        );

        when(getUserUseCase.execute(any(UserId.class))).thenReturn(result);

        mockMvc.perform(get("/api/v1/users/{id}", userId.value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.value()))
                .andExpect(jsonPath("$.username").value("johnsmith"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getUserWithUnknownIdShouldReturn404() throws Exception {
        String unknownId = "00000000-0000-0000-0000-000000000000";
        when(getUserUseCase.execute(any(UserId.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(get("/api/v1/users/{id}", unknownId))
                .andExpect(status().isNotFound());
    }

    @Test
    void listUsersShouldReturn200WithUserList() throws Exception {
        UserResult user1 = new UserResult(
                "id-1", "johnsmith", "john@example.com",
                "John", "Smith", "ACTIVE", "MALE", LocalDate.of(2000, 1, 1)
        );
        UserResult user2 = new UserResult(
                "id-2", "janedoe", "jane@example.com",
                "Jane", "Doe", "ACTIVE", "FEMALE", LocalDate.of(1999, 6, 15)
        );

        when(listUsersUseCase.execute()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("johnsmith"))
                .andExpect(jsonPath("$[1].username").value("janedoe"));
    }

    @Test
    void changeStatusShouldReturn200WithUpdatedUser() throws Exception {
        ChangeUserStatusRequest request = new ChangeUserStatusRequest("INACTIVE");
        UserId userId = UserId.generate();
        UserResult result = new UserResult(
                userId.value(), "johnsmith", "john@example.com",
                "John", "Smith", "INACTIVE", "MALE", LocalDate.of(2000, 1, 1)
        );

        doNothing().when(changeUserStatusUseCase).execute(any(ChangeUserStatusCommand.class));
        when(getUserUseCase.execute(any(UserId.class))).thenReturn(result);

        mockMvc.perform(patch("/api/v1/users/{id}/status", userId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void changeStatusWithInvalidTransitionShouldReturn409() throws Exception {
        ChangeUserStatusRequest request = new ChangeUserStatusRequest("BLOCKED");
        UserId userId = UserId.generate();

        doThrow(new IllegalStateException("User already blocked"))
                .when(changeUserStatusUseCase).execute(any(ChangeUserStatusCommand.class));

        mockMvc.perform(patch("/api/v1/users/{id}/status", userId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void changePasswordShouldReturn204() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword123");
        UserId userId = UserId.generate();

        when(passwordEncoder.encode(any())).thenReturn("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        doNothing().when(changePasswordUseCase).execute(any(ChangePasswordCommand.class));

        mockMvc.perform(patch("/api/v1/users/{id}/password", userId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    private static CreateUserRequest validCreateUserRequest() {
        return new CreateUserRequest(
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
}
