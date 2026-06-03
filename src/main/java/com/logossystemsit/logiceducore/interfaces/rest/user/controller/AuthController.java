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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
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
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario con su membresía inicial y devuelve token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario con email y contraseña, devuelve token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Email email = new Email(request.email());

        var user = userRepository.findByEmail(email)
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

        return new CreateUserCommand(userId, email, passwordHash, name, sex, birthDate, document, role, scope);
    }
}
