# User Auth

## 1. Database Schema

### Requirement: Flyway Migrations

The system MUST provide the users table via Flyway migration V1.

| Table | PK | Key columns | Constraints |
|-------|----|-------------|-------------|
| `users` | id | username, email, password_hash, name, sex, birth_date, document_type, document_number, status, timestamps | UNIQUE(username, email), NOT NULL cols |

- GIVEN blank PostgreSQL WHEN migration runs THEN both tables exist with correct schema

---

## 2. Registration

### Requirement: Register

POST /api/v1/auth/register MUST validate input, hash password (BCrypt), persist user+initial membership atomically, return 201.

- GIVEN valid data WHEN register THEN 201 with userId + username, password BCrypt-hashed
- GIVEN duplicate email WHEN register THEN 409
- GIVEN duplicate username WHEN register THEN 409

---

## 3. Login

### Requirement: Login

POST /api/v1/auth/login MUST verify credentials and return signed JWT with userId + active memberships.

- GIVEN valid credentials WHEN login THEN 200 with JWT
- GIVEN wrong password WHEN login THEN 401
- GIVEN unknown email WHEN login THEN 401

---

## 4. JWT Validation

### Requirement: JWT Validation

Protected /api/v1/** endpoints MUST reject requests without valid JWT.

- GIVEN no token WHEN /api/v1/users/me THEN 401
- GIVEN valid JWT WHEN same endpoint THEN 200
- GIVEN expired JWT THEN 401
- GIVEN malformed JWT THEN 401

---

## 5. Infrastructure

### Requirement: Password Hashing

BCrypt MUST hash passwords on persist and verify during login.

- GIVEN plaintext password WHEN register THEN DB stores BCrypt hash, not plaintext
- GIVEN wrong password during login THEN PasswordEncoder.matches returns false

### Requirement: Stateless Sessions

Authentication MUST be stateless (no HTTP session, no CSRF).

- WHEN SecurityContext is configured THEN SessionCreationPolicy is STATELESS
- WHEN CSRF config is applied THEN csrf.disable() is called

---

## 6. Dependencies

This capability depends on:
- **Database Schema** (users table via Flyway V1)
- **User Management** (user entity and repository must exist)
