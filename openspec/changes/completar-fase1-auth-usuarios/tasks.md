# Tasks: Completar Fase 1 — Auth + Usuarios

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~2000 (prod ~1100 + tests ~900) |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | 6 work units per table below |
| Delivery strategy | ask-on-risk |
| Chain strategy | pending |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: feature-branch-chain
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Base | Est. lines |
|------|------|------|------------|
| 1 | Build + Flyway + fix repo gaps | main | ~200 |
| 2 | JWT security infra | main | ~193 |
| 3 | New app-layer use cases + @Transactional | main | ~165 |
| 4 | HexagonalConfig + REST DTOs | main | ~310 |
| 5 | REST Controllers | main | ~240 |
| 6 | Tests (unit + service + integration) | main | ~900 |

## Phase 1: Foundation — Build, Flyway, Fix Repo Gaps

- [x] 1.1 Add jjwt-api, jjwt-impl, jjwt-jackson to `build.gradle.kts`
- [x] 1.2 Add jwt.secret, jwt.expiration-ms to `application.yaml`; set `ddl-auto: none`
- [x] 1.3 Create `V1__create_users_table.sql` with schema per design
- [x] 1.4 Create `V2__create_memberships_table.sql` with FK + index
- [x] 1.5 Add findByEmail, existsByEmail, existsByUsername to `UserRepository` port
- [x] 1.6 Add derived query methods to `UserJpaRepository`
- [x] 1.7 Implement new port methods in `UserRepositoryAdapter`
- [x] 1.8 Fix `MembershipRepositoryAdapter.findById` — implement real JPA query

## Phase 2: JWT Security Infrastructure

- [x] 2.1 Create `JwtProperties` @ConfigurationProperties(prefix="jwt")
- [x] 2.2 Create `JwtService` with generate(), validate(), extractUserId(), extractMemberships()
- [x] 2.3 Create `JwtAuthenticationFilter` (OncePerRequestFilter) — Bearer token → SecurityContext
- [x] 2.4 Create `SecurityConfig` — /auth/** public, /api/v1/** authenticated, stateless

## Phase 3: New Application Use Cases + @Transactional

- [x] 3.1 Create `AuthenticateUserUseCase` port + `AuthenticateUserService` (password verify + JWT)
- [x] 3.2 Create `LoginCommand` record and `LoginResult` record
- [x] 3.3 Create `ListUsersUseCase` port + `ListUsersService` (findAll → UserResult)
- [x] 3.4 Create `ChangeMembershipRoleUseCase` port + `ChangeMembershipRoleService`
- [x] 3.5 Add @Transactional to CreateUserService, ChangeUserStatusService, ChangePasswordService
- [x] 3.6 Add @Transactional to AssignMembershipService, ToggleMembershipService, ChangeMembershipScopeService
- [x] 3.7 Add @Transactional(readOnly=true) to GetUserService, GetUserMembershipsService

## Phase 4: HexagonalConfig + REST DTOs

- [x] 4.1 Create `HexagonalConfig.java` wiring Clock, repos, all services, security beans
- [x] 4.2 Create request DTOs: RegisterRequest, LoginRequest, CreateUserRequest
- [x] 4.3 Create request DTOs: ChangeUserStatusRequest, ChangePasswordRequest
- [x] 4.4 Create request DTOs: AssignMembershipRequest, ChangeMembershipRoleRequest, ChangeMembershipScopeRequest
- [x] 4.5 Create response DTOs: AuthResponse, UserResponse, MembershipResponse

## Phase 5: REST Controllers

- [x] 5.1 Create `AuthController` — POST /auth/register, POST /auth/login
- [x] 5.2 Create `UserController` — POST, GET /{id}, GET, PATCH status, PATCH password
- [x] 5.3 Create `MembershipController` — POST, GET /users/{id}/memberships, DELETE, PUT activate, PATCH role/scope

## Phase 6: Tests

- [x] 6.1 Domain unit tests for User (create, restore, status transitions, password, VO validation)
- [x] 6.2 Domain unit tests for Membership (create, role-scope compatibility, activate/deactivate)
- [x] 6.3 Service tests for all user use cases with mocked repos
- [x] 6.4 Service tests for all membership use cases with mocked repos
- [x] 6.5 @WebMvcTest for AuthController, UserController, MembershipController (covered by standalone MockMvc tests from Phase 5 + existing security filter tests)
- [x] 6.6 @DataJpaTest for repositories (CRUD, constraints, FK violations — H2 in-memory with Hibernate DDL unique constraints)
- [x] 6.7 Security tests: missing/expired/invalid tokens, endpoint access rules (covered by JwtServiceTest, JwtAuthenticationFilterTest, SecurityConfigTest from Phase 5)
