# User Management

## 1. Database Schema

### Requirement: Flyway Migrations

The system MUST provide the users table via Flyway migration V1.

| Table | PK | Key columns | Constraints |
|-------|----|-------------|-------------|
| `users` | id | username, email, password_hash, name, sex, birth_date, document_type, document_number, status, timestamps | UNIQUE(username, email), NOT NULL cols |

- GIVEN blank PostgreSQL WHEN migration runs THEN both tables exist with correct schema

---

## 2. Create User

### Requirement: Create User

POST /api/v1/users MUST validate document type vs age, check email/username uniqueness, persist.

- GIVEN valid request WHEN POST THEN 201 with userId
- GIVEN minor with CC document THEN 422
- GIVEN duplicate email WHEN POST THEN 409

---

## 3. Get User

### Requirement: Get User

GET /api/v1/users/{id} MUST return user or 404.

- GIVEN existing user WHEN GET THEN 200 with user data
- GIVEN unknown id THEN 404

---

## 4. List Users

### Requirement: List Users

GET /api/v1/users SHOULD return a paginated list of users.

- WHEN GET /api/v1/users THEN 200 with list

---

## 5. Change Status

### Requirement: Change Status

PATCH /api/v1/users/{id}/status MUST support activate/deactivate/block transitions.

- GIVEN INACTIVE user WHEN patch ACTIVE THEN 200
- GIVEN already BLOCKED user WHEN patch BLOCKED THEN 409
- GIVEN BLOCKED user WHEN patch ACTIVE THEN 200 (recoverable)

---

## 6. Change Password

### Requirement: Change Password

PATCH /api/v1/users/{id}/password MUST hash the new password and reject if same as current or user is blocked.

- GIVEN active user with new password THEN 200
- GIVEN blocked user THEN 403
- GIVEN same password as current THEN 422

---

## 7. Infrastructure

### Requirement: @Transactional

Every mutable service method MUST be @Transactional; reads MUST use @Transactional(readOnly = true).

- GIVEN CreateUserService WHEN user save succeeds but membership fails THEN rollback both
- GIVEN read-only query WHEN called THEN readOnly=true is set

### Requirement: Document-Age Policy

User creation MUST validate document type vs age (CC requires age >= 18, TI requires age < 18).

- GIVEN minor with CC THEN 422
- GIVEN adult with TI THEN 422
- GIVEN minor with TI THEN 201

---

## 8. Dependencies

This capability depends on:
- **Database Schema** (users table via Flyway V1)
- **User Auth** (JWT security for protected endpoints)
