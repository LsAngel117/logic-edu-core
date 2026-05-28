package com.logossystemsit.logiceducore.interfaces.rest.user.controller;

import com.logossystemsit.logiceducore.application.user.dto.command.*;
import com.logossystemsit.logiceducore.application.user.dto.result.CreateUserResult;
import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.application.user.port.in.*;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.interfaces.rest.dto.response.UserResponse;
import com.logossystemsit.logiceducore.interfaces.rest.user.dto.request.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final ChangeUserStatusUseCase changeUserStatusUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final PasswordEncoder passwordEncoder;

    public UserController(CreateUserUseCase createUserUseCase,
                          GetUserUseCase getUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          ChangeUserStatusUseCase changeUserStatusUseCase,
                          ChangePasswordUseCase changePasswordUseCase,
                          PasswordEncoder passwordEncoder) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.changeUserStatusUseCase = changeUserStatusUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        CreateUserCommand command = mapToCreateUserCommand(request);
        CreateUserResult result = createUserUseCase.execute(command);

        UserResult user = getUserUseCase.execute(result.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponse(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        UserId userId;
        try {
            userId = new UserId(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user id format");
        }
        try {
            UserResult result = getUserUseCase.execute(userId);
            return ResponseEntity.ok(toUserResponse(result));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<UserResult> results = listUsersUseCase.execute();
        List<UserResponse> responses = results.stream()
                .map(this::toUserResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> changeStatus(@PathVariable String id,
                                                     @RequestBody ChangeUserStatusRequest request) {
        try {
            UserId userId = new UserId(id);
            User.Status status = User.Status.valueOf(request.status().toUpperCase());
            ChangeUserStatusCommand command = new ChangeUserStatusCommand(userId, status);

            changeUserStatusUseCase.execute(command);

            UserResult result = getUserUseCase.execute(userId);
            return ResponseEntity.ok(toUserResponse(result));
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String id,
                                               @RequestBody ChangePasswordRequest request) {
        UserId userId = new UserId(id);
        PasswordHash newHash = new PasswordHash(passwordEncoder.encode(request.newPassword()));
        ChangePasswordCommand command = new ChangePasswordCommand(userId, newHash);

        changePasswordUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    private CreateUserCommand mapToCreateUserCommand(CreateUserRequest r) {
        UserId userId = UserId.generate();
        Username username = new Username(r.username());
        Email email = new Email(r.email());
        PasswordHash passwordHash = new PasswordHash(passwordEncoder.encode(r.rawPassword()));
        Name name = new Name(r.firstGivenName(), r.secondGivenName(), r.firstFamilyName(), r.secondFamilyName());
        User.Sex sex = User.Sex.valueOf(r.sex().toUpperCase());
        LocalDate birthDate = LocalDate.parse(r.birthDate());
        Document document = new Document(
                Document.DocumentType.valueOf(r.documentType()),
                new DocumentNumber(r.documentValue())
        );
        Role role = Role.valueOf(r.role().toUpperCase());
        Scope scope = Scope.from(Scope.Type.valueOf(r.scopeType().toUpperCase()), r.scopeRefId());

        return new CreateUserCommand(userId, username, email, passwordHash, name, sex, birthDate, document, role, scope);
    }

    private UserResponse toUserResponse(UserResult result) {
        return new UserResponse(
                result.id(),
                result.username(),
                result.email(),
                result.firstName() + " " + result.lastName(),
                result.status(),
                LocalDate.now().toString()
        );
    }
}
