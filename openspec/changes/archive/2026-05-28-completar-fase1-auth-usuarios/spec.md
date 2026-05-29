# Complete Phase 1 — Auth + Usuarios

## 1. Database Schema

### Requirement: Flyway Migrations

The system MUST apply V1 (users) and V2 (memberships) Flyway migrations.

| Table | PK | Key columns | Constraints |
|-------|----|-------------|-------------|
| `users` | id | username, email, password_hash, name, sex, birth_date, document_type, document_number, status, timestamps | UNIQUE(username, email), NOT NULL cols |
| `memberships` | id | user_id (FK→users), role, scope_type, scope_ref_id, active | NOT NULL, FK |

- GIVEN blank PostgreSQL WHEN migration runs THEN both tables exist with correct schema
- GIVEN no user WHEN insert membership with that user_id THEN FK violation

---

## 2. User Auth

### Requirement: Register

POST /api/v1/auth/register MUST validate input, hash password (BCrypt), persist user+membership atomically, return 201.

- GIVEN valid data WHEN register THEN 201 with userId + username, password BCrypt-hashed
- GIVEN duplicate email WHEN register THEN 409

### Requirement: Login

POST /api/v1/auth/login MUST verify credentials and return signed JWT with userId + active memberships.

- GIVEN valid credentials WHEN login THEN 200 with JWT
- GIVEN wrong password WHEN login THEN 401

### Requirement: JWT Validation

Protected /api/v1/** endpoints MUST reject requests without valid JWT.

- GIVEN no token WHEN /api/v1/users/me THEN 401
- GIVEN valid JWT WHEN same endpoint THEN 200
- GIVEN expired JWT THEN 401

---

## 3. User Management

### Requirement: Create User

POST /api/v1/users MUST validate document vs age, check email/username uniqueness, persist.

- GIVEN valid request WHEN POST THEN 201 with userId
- GIVEN minor with CC document THEN 422

### Requirement: Get User

GET /api/v1/users/{id} MUST return user or 404.

- GIVEN existing user WHEN GET THEN 200 with user data
- GIVEN unknown id THEN 404

### Requirement: List Users

GET /api/v1/users SHOULD return paginated list.

- WHEN GET /api/v1/users THEN 200 with list

### Requirement: Change Status

PATCH /api/v1/users/{id}/status MUST support activate/deactivate/block transitions.

- GIVEN INACTIVE user WHEN patch ACTIVE THEN 200
- GIVEN already BLOCKED user WHEN patch BLOCKED THEN 409

### Requirement: Change Password

PATCH /api/v1/users/{id}/password MUST hash, reject if same as current or user blocked.

- GIVEN active user with new password THEN 200
- GIVEN blocked user THEN 403

---

## 4. Membership Management

### Requirement: Assign

POST /api/v1/memberships MUST validate role-scope compatibility.

- GIVEN user WHEN POST PLATFORM_ADMIN/PLATFORM THEN 201
- GIVEN TEACHER with SCHOOL scope THEN 422

### Requirement: Toggle

DELETE /api/v1/memberships/{id} MUST deactivate. PUT /api/v1/memberships/{id}/activate MUST reactivate.

- GIVEN active membership WHEN DELETE THEN 204, membership inactive
- GIVEN user's last active membership WHEN deactivate THEN 422

### Requirement: List by User

GET /api/v1/users/{id}/memberships MUST return all memberships for the user.

- GIVEN user with 2 memberships WHEN GET THEN 200 with 2 items

### Requirement: Change Role/Scope

PATCH /api/v1/memberships/{id}/role and /scope MUST re-validate compatibility.

- GIVEN SCHOOL_ADMIN membership WHEN patch SCOPE to another school THEN 200
- GIVEN incompatible role+scope THEN 422

---

## 5. Infrastructure

### Requirement: @Transactional

Every mutable service method MUST be @Transactional; reads MUST use @Transactional(readOnly = true).

- GIVEN CreateUserService WHEN user save succeeds but membership fails THEN rollback both

### Requirement: Spring Wiring

@Configuration classes MUST wire all hexagonal layers. Clock bean MUST be available for time ops.

- WHEN context starts THEN all beans (repos, services, mappers, security) load without error

### Requirement: Password Hashing

BCrypt MUST hash passwords on persist and verify during login.

- GIVEN plaintext password WHEN register THEN DB stores BCrypt hash, not plaintext

---

## 6. Tests

### Requirement: Domain Unit Tests

Every aggregate behavior and validation rule MUST be tested.

- GIVEN User/Membership classes WHEN ./gradlew test THEN all factory methods, behavior methods, and validation rules covered

### Requirement: Service Tests

All use cases MUST have tests with mocked repositories.

- GIVEN mocked repos WHEN service methods called THEN success and error paths complete

### Requirement: Controller Tests

@WebMvcTest slices MUST verify HTTP status codes and security filtering for every endpoint.
