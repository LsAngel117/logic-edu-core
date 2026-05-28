package com.logossystemsit.logiceducore.interfaces.rest.user.controller;

import com.logossystemsit.logiceducore.application.user.dto.command.CreateUserCommand;
import com.logossystemsit.logiceducore.application.user.dto.command.LoginCommand;
import com.logossystemsit.logiceducore.application.user.dto.result.CreateUserResult;
import com.logossystemsit.logiceducore.application.user.dto.result.LoginResult;
import com.logossystemsit.logiceducore.application.user.port.in.AuthenticateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.in.CreateUserUseCase;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.infrastructure.security.service.JwtService;
import com.logossystemsit.logiceducore.interfaces.rest.dto.response.AuthResponse;
import com.logossystemsit.logiceducore.interfaces.rest.user.dto.request.LoginRequest;
import com.logossystemsit.logiceducore.interfaces.rest.user.dto.request.RegisterRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CreateUserUseCase createUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(CreateUserUseCase createUserUseCase,
                          AuthenticateUserUseCase authenticateUserUseCase,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.createUserUseCase = createUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(new Username(request.username()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (userRepository.existsByEmail(new Email(request.email()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        CreateUserCommand command = mapToCreateUserCommand(request);
        CreateUserResult result = createUserUseCase.execute(command);

        String token = jwtService.generate(result.userId().value(), List.of());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, result.userId().value(), result.username()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Username username = new Username(request.username());

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        LoginCommand command = new LoginCommand(user.getId(), request.rawPassword());

        try {
            LoginResult result = authenticateUserUseCase.execute(command);
            return ResponseEntity.ok(new AuthResponse(result.token(), result.userId().value(), result.username()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    private CreateUserCommand mapToCreateUserCommand(RegisterRequest r) {
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
}
