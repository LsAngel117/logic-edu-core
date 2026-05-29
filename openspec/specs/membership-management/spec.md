# Membership Management

## 1. Database Schema

### Requirement: Flyway Migrations

The system MUST provide the memberships table via Flyway migration V2.

| Table | PK | Key columns | Constraints |
|-------|----|-------------|-------------|
| `memberships` | id | user_id (FK→users), role, scope_type, scope_ref_id, active | NOT NULL, FK |

- GIVEN blank PostgreSQL WHEN migration runs THEN both tables exist with correct schema
- GIVEN no user WHEN insert membership with that user_id THEN FK violation

---

## 2. Assign Membership

### Requirement: Assign

POST /api/v1/memberships MUST validate role-scope compatibility before creating.

- GIVEN user WHEN POST PLATFORM_ADMIN/PLATFORM THEN 201
- GIVEN TEACHER with SCHOOL scope THEN 422
- GIVEN incompatible role+scope pair THEN 422

---

## 3. Toggle Membership

### Requirement: Deactivate

DELETE /api/v1/memberships/{id} MUST deactivate the membership.

- GIVEN active membership WHEN DELETE THEN 204, membership inactive
- GIVEN user's last active membership WHEN deactivate THEN 422

### Requirement: Reactivate

PUT /api/v1/memberships/{id}/activate MUST reactivate a deactivated membership.

- GIVEN inactive membership WHEN PUT activate THEN 200, membership active
- GIVEN already active membership WHEN PUT activate THEN 200 (idempotent)

---

## 4. List by User

### Requirement: List by User

GET /api/v1/users/{id}/memberships MUST return all memberships for the user.

- GIVEN user with 2 memberships WHEN GET THEN 200 with 2 items
- GIVEN user with no memberships WHEN GET THEN 200 with empty list

---

## 5. Change Role

### Requirement: Change Role

PATCH /api/v1/memberships/{id}/role MUST re-validate role-scope compatibility.

- GIVEN valid new role for current scope THEN 200
- GIVEN incompatible role+scope combination THEN 422

---

## 6. Change Scope

### Requirement: Change Scope

PATCH /api/v1/memberships/{id}/scope MUST re-validate role-scope compatibility.

- GIVEN SCHOOL_ADMIN membership WHEN patch SCOPE to another school THEN 200
- GIVEN incompatible role+scope THEN 422

---

## 7. Infrastructure

### Requirement: @Transactional

Every mutable membership service method MUST be @Transactional.

- GIVEN deactivate operation WHEN called THEN membership state change is atomic
- GIVEN assign operation WHEN called THEN creation is atomic

### Requirement: Last-Membership Guard

The system MUST prevent deactivating a user's only active membership.

- GIVEN user with 1 active membership WHEN deactivate THEN 422 with error message
- GIVEN user with 2+ active memberships WHEN deactivate one THEN 204

---

## 8. Dependencies

This capability depends on:
- **Database Schema** (users + memberships tables via Flyway V1 and V2)
- **User Management** (user entity must exist for FK reference)
- **User Auth** (JWT security for protected endpoints)
