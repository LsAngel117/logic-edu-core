# Design: Completar Fase 1 — Auth + Usuarios

## Technical Approach

Wire the existing domain/application layers to REST and JPA via Spring Boot. Create Flyway schemas, add `@Transactional` annotations, build JWT security (jjwt + OncePerRequestFilter), and expose REST controllers under `/api/v1`. Existing use cases (CreateUser, GetUser, ChangeUserStatus, etc.) are already implemented — this phase adds the hex adapters that connect them.

## Architecture Decisions

| Decision | Option A | Option B | Choice | Rationale |
|----------|----------|----------|--------|-----------|
| Mapper location | `application/{subdomain}/` as Spring `@Component` | Static methods in DTOs | **A** | Follows existing `UserResult.from()` pattern; injectable for testability |
| JWT library | jjwt (io.jsonwebtoken) | Nimbus | **jjwt** | Proposal specifies it; simpler API; no extra deps |
| Register flow | `AuthController` calls `CreateUserUseCase` + `JwtService` inline | Separate `RegisterUseCase` port | **Inline orchestration** | Avoids over-abstracting Phase 1; controller delegates to use case, then calls JWT service |
| Config beans | Single `HexagonalConfig` + `ClockConfig` | Per-layer configs | **Single config** | Proposal specifies it; keeps wiring visible in one place |
| Request DTOs vs Commands | Controllers receive `*Request` DTOs, map to `*Command` record, pass to use case | Controllers receive Commands directly | **Request DTOs** | Request DTOs isolate API shape from domain; `*Command` records take domain VOs |
| JPA repos | Exists-by-X via `boolean existsByEmail(String)` | Return `Optional<UserEntity>` for existence checks | **boolean** | Spring Data derived query, zero code, fast |
| Login use case port | `AuthenticateUserUseCase` interface | None (controller does it) | **Interface** | Login involves password verification + JWT generation — testable use case, worth a port |

## Data Flow

```
POST /auth/login
  Request
    → AuthController (maps LoginRequest → AuthenticateUserCommand)
    → AuthenticateUserService
      → UserRepository.findByEmail(email)
      → PasswordEncoder.matches(raw, hash)
      → JwtService.generate(userId, memberships)
    → AuthResponse(jwt, userId)

POST /auth/register
  Request
    → AuthController (maps RegisterRequest → CreateUserCommand)
    → CreateUserUseCase (creates User + Membership atomically)
    → JwtService.generate()
    → AuthResponse(jwt, userId)

GET /users/{id}
    → UserController → GetUserUseCase → UserRepository.findById → UserResponse
```

## DB Schema (Flyway)

**V1__create_users_table.sql**
```sql
CREATE TABLE users (
    id              VARCHAR(36) PRIMARY KEY,
    username        VARCHAR(21) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_given_name  VARCHAR(50) NOT NULL,
    second_given_name VARCHAR(50),
    first_family_name VARCHAR(50) NOT NULL,
    second_family_name VARCHAR(50),
    sex             VARCHAR(10) NOT NULL,
    birth_date      DATE NOT NULL,
    document_type   VARCHAR(10) NOT NULL,
    document_value  VARCHAR(20) NOT NULL,
    status          VARCHAR(10) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
```

**V2__create_memberships_table.sql**
```sql
CREATE TABLE memberships (
    id            VARCHAR(36) PRIMARY KEY,
    user_id       VARCHAR(36) NOT NULL REFERENCES users(id),
    role          VARCHAR(30) NOT NULL,
    scope_type    VARCHAR(20) NOT NULL,
    scope_ref_id  VARCHAR(36),
    active        BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_memberships_user_id ON memberships(user_id);
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `src/main/resources/db/migration/V1__create_users_table.sql` | Create | Users table + indexes per schema above |
| `src/main/resources/db/migration/V2__create_memberships_table.sql` | Create | Memberships table + FK + index per schema above |
| `infrastructure/config/HexagonalConfig.java` | Create | @Configuration: wires Clock bean, all use cases, adapter, security beans |
| `infrastructure/security/JwtService.java` | Create | generate(), validate(), extractUserId(), extractMemberships() using jjwt HMAC-SHA256 |
| `infrastructure/security/JwtProperties.java` | Create | @ConfigurationProperties("jwt"): secret, expiration-ms |
| `infrastructure/security/JwtAuthenticationFilter.java` | Create | OncePerRequestFilter: extract Authorization header, validate token, set SecurityContext |
| `infrastructure/security/SecurityConfig.java` | Create | SecurityFilterChain: /auth/** public, /api/v1/** authenticated, stateless sessions |
| `application/user/port/in/AuthenticateUserUseCase.java` | Create | Interface: `LoginResult execute(LoginCommand)` |
| `application/user/usecase/AuthenticateUserService.java` | Create | Verifies password via PasswordEncoder, generates JWT |
| `application/user/dto/command/LoginCommand.java` | Create | record(Email email, String rawPassword) |
| `application/user/dto/result/LoginResult.java` | Create | record(UserId userId, String jwt) |
| `application/user/port/in/ListUsersUseCase.java` | Create | Interface: `List<UserResult> execute()` |
| `application/user/usecase/ListUsersService.java` | Create | findAll() → map to UserResult |
| `application/membership/port/in/ChangeMembershipRoleUseCase.java` | Create | Interface: `void execute(ChangeMembershipRoleCommand)` |
| `application/membership/usecase/ChangeMembershipRoleService.java` | Create | Implements role change |
| `interfaces/rest/controller/AuthController.java` | Create | POST /auth/register, POST /auth/login |
| `interfaces/rest/controller/UserController.java` | Create | POST /users, GET /users/{id}, GET /users, PATCH status/password |
| `interfaces/rest/controller/MembershipController.java` | Create | POST /memberships, GET /users/{id}/memberships, DELETE toggle, PUT activate, PATCH role/scope |
| `interfaces/rest/dto/request/*.java` | Create | RegisterRequest, LoginRequest, CreateUserRequest, ChangeUserStatusRequest, ChangePasswordRequest, AssignMembershipRequest, ChangeMembershipRoleRequest, ChangeMembershipScopeRequest |
| `interfaces/rest/dto/response/*.java` | Create | AuthResponse, UserResponse, MembershipResponse |
| `build.gradle.kts` | Modify | Add jjwt-api, jjwt-impl, jjwt-jackson dependencies |
| `application.yaml` | Modify | Add jwt.secret, jwt.expiration-ms, set ddl-auto=none |
| `application/user/port/out/UserRepository.java` | Modify | Add findByEmail, existsByEmail, existsByUsername |
| `infrastructure/user/persistence/repository/UserJpaRepository.java` | Modify | Add derived query methods |
| `infrastructure/user/persistence/adapter/UserRepositoryAdapter.java` | Modify | Implement new port methods |
| `infrastructure/membership/persistence/adapter/MembershipRepositoryAdapter.java` | Modify | Implement findById (currently returns Optional.empty()) |
| 8 service classes in application/*/usecase/ | Modify | Add @Transactional / @Transactional(readOnly=true) |

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Domain unit | User: create, restore, block/activate/deactivate, changePassword validation, VO validation rules | JUnit 5 + AssertJ |
| Domain unit | Membership: create, role-scope compatibility, activate/deactivate, changeRole/changeScope | JUnit 5 |
| Application service | All services: mock UserRepository/MembershipRepository/Clock/PasswordEncoder, test success + error paths | Mockito, @ExtendWith |
| Controller integration | @WebMvcTest slices with mocked use cases: verify HTTP status codes, request validation, security filter behavior | @WebMvcTest, MockMvc |
| Repository | @DataJpaTest: verify CRUD, uniqueness constraints, FK violations | @DataJpaTest, Testcontainers or H2 |
| Security | Filter unit test: missing/expired/invalid tokens; SecurityConfig: public vs protected endpoints | MockMvc with @WithMockUser |

## @Transactional Rules

- `@Transactional` on: CreateUserService, ChangeUserStatusService, ChangePasswordService, AssignMembershipService, ToggleMembershipService, ChangeMembershipScopeService, ChangeMembershipRoleService, AuthenticateUserService
- `@Transactional(readOnly = true)` on: GetUserService, ListUsersService, GetUserMembershipsService
- JwtService: NO transactional (stateless)

## Open Questions

- [ ] Secret rotation strategy: in Phase 1, externalize via `application.yaml` with comment; later integrate with Vault/Secrets Manager
- [ ] Refresh token: out of scope per proposal, but JWT expiration UX needs documentation
