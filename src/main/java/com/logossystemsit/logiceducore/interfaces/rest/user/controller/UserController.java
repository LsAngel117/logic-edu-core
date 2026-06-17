package com.logossystemsit.logiceducore.interfaces.rest.user.controller;

import com.logossystemsit.logiceducore.application.user.dto.command.*;
import com.logossystemsit.logiceducore.application.user.dto.result.CreateUserResult;
import com.logossystemsit.logiceducore.application.user.dto.result.UserResult;
import com.logossystemsit.logiceducore.application.user.port.in.*;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Role;
import com.logossystemsit.logiceducore.domain.membership.model.valueobject.Scope;
import com.logossystemsit.logiceducore.domain.user.model.User;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.*;
import com.logossystemsit.logiceducore.shared.valueobject.*;
import com.logossystemsit.logiceducore.interfaces.rest.dto.response.UserResponse;
import com.logossystemsit.logiceducore.interfaces.rest.user.dto.request.*;

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
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios de la plataforma")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final ChangeUserStatusUseCase changeUserStatusUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final PasswordEncoder passwordEncoder;

    public UserController(CreateUserUseCase createUserUseCase,
                          GetUserUseCase getUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          ChangeUserStatusUseCase changeUserStatusUseCase,
                          ChangePasswordUseCase changePasswordUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          PasswordEncoder passwordEncoder) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.changeUserStatusUseCase = changeUserStatusUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario en la plataforma")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recurso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        CreateUserCommand command = mapToCreateUserCommand(request);
        CreateUserResult result = createUserUseCase.execute(command);

        UserResult user = getUserUseCase.execute(result.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponse(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario", description = "Consulta un usuario por su identificador único")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Modifica la información básica de un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id,
                                                    @RequestBody UpdateUserRequest request) {
        UserId userId = new UserId(id);
        UpdateUserCommand command = mapToUpdateUserCommand(request, userId);
        UserResult result = updateUserUseCase.execute(command);
        return ResponseEntity.ok(toUserResponse(result));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<UserResult> results = listUsersUseCase.execute();
        List<UserResponse> responses = results.stream()
                .map(this::toUserResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estado", description = "Activa, desactiva o bloquea un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
    @Operation(summary = "Cambiar contraseña", description = "Actualiza la contraseña de un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operación exitosa sin contenido"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para ejecutar la acción"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto de regla de negocio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
        Phone phone = r.phone() != null && !r.phone().isBlank() ? Phone.of(r.phone()) : null;
        Address address = r.address() != null && !r.address().isBlank() ? Address.of(r.address()) : null;
        City city = r.city() != null && !r.city().isBlank() ? new City(r.city()) : null;
        Country country = r.country() != null && !r.country().isBlank() ? new Country(r.country()) : null;

        return new CreateUserCommand(userId, email, passwordHash, name, sex, birthDate, document,
                phone, address, city, country, role, scope);
    }

    private UserResponse toUserResponse(UserResult result) {
        return new UserResponse(
                result.id(),
                result.username(),
                result.email(),
                result.firstName() + " " + result.lastName(),
                result.status(),
                LocalDate.now().toString(),
                result.phone(),
                result.address(),
                result.city(),
                result.country()
        );
    }

    private UpdateUserCommand mapToUpdateUserCommand(UpdateUserRequest r, UserId userId) {
        Email email = new Email(r.email());
        Name name = new Name(r.firstGivenName(), r.secondGivenName(), r.firstFamilyName(), r.secondFamilyName());
        User.Sex sex = User.Sex.valueOf(r.sex().toUpperCase());
        LocalDate birthDate = LocalDate.parse(r.birthDate());
        Document document = new Document(
                Document.DocumentType.valueOf(r.documentType()),
                new DocumentNumber(r.documentValue())
        );
        Phone phone = r.phone() != null ? Phone.of(r.phone()) : null;
        Address address = r.address() != null ? Address.of(r.address()) : null;
        City city = r.city() != null ? new City(r.city()) : null;
        Country country = r.country() != null ? new Country(r.country()) : null;
        return new UpdateUserCommand(userId, email, name, sex, birthDate, document, phone, address, city, country);
    }
}
