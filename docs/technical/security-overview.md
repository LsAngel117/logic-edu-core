# Security Overview

## 1. Authentication

The system uses JWT (JSON Web Tokens) for authentication.

### Rules

- Every request must include a valid JWT token.
- Tokens must be signed and verified on every request.
- Tokens must contain:
  - userId
  - active memberships (or reference to them)
- Expired or invalid tokens must be rejected.

---

## 2. Authorization Model

Authorization is based on **Membership + Scope + Role**.

### Core Concepts

- **User**: authenticated identity
- **Membership**: defines a user's role within a scope
- **Role**: defines permissions
- **Scope**: defines where the role applies

### Scope Types

- PLATFORM
- SCHOOL
- BRANCH
- COURSE

---

## 3. Roles

- PLATFORM_ADMIN
- SCHOOL_ADMIN
- BRANCH_ADMIN
- TEACHER
- STUDENT

Each role is restricted to specific scope types.

---

## 4. Enforcement Rules

- Authorization must always be validated in the backend.
- Frontend validation is optional and not trusted.
- Every use case must validate:
  - authentication
  - authorization
  - scope access

---

## 5. Multi-Tenancy Isolation

- Data must be isolated by school.
- A user cannot access data outside their scope.
- Cross-school access is only allowed for PLATFORM_ADMIN.

---

## 6. Security Principles

- Deny by default.
- Explicit permission is required for every action.
- No implicit access based on UI or assumptions.
- All critical operations must be auditable.

---

## 7. Responsibility by Layer

### Domain
- Defines roles and scope compatibility.
- Does NOT handle authorization decisions.

### Application
- Enforces authorization policies.
- Validates access before executing use cases.

### Infrastructure
- Handles JWT validation.
- Provides authentication mechanisms.
