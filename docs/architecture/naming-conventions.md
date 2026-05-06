# Naming Conventions

This document defines the naming conventions for the project to ensure **consistency, readability, and scalability** across all layers.

---

## 1. General Principles

* Use **business language**, not technical jargon.
* Names must reflect **intent, not implementation**.
* Be **explicit over implicit**.
* Avoid abbreviations unless they are standard (e.g., DTO, ID).
* Keep naming consistent across all layers.

---

## 2. Domain Layer

### 2.1 Entities / Aggregates

* Use singular nouns.
* Avoid suffixes like `Entity`, `Model`.

```text
User
School
Branch
Membership
Group
Enrollment
```

---

### 2.2 Value Objects

* Use descriptive names.
* Prefer prefixing with the aggregate when needed.

```text
UserId
Email
Username
SchoolName
BranchCode
```

---

### 2.3 Domain Services

* Use names that express **business logic**.

```text
UsernameGenerator
UserCreationPolicy
MembershipDomainService
```

---

## 3. Application Layer

### 3.1 Use Cases

* Format:

```text
<Action><Entity>UseCase
```

* Examples:

```text
CreateUserUseCase
CreateSchoolUseCase
CreateBranchUseCase
AssignRoleUseCase
EnrollStudentUseCase
```

---

### 3.2 Use Case Implementations

* Use suffix `Impl` **only if implementing a port/interface**.

```text
CreateUserUseCaseImpl
```

---

### 3.3 DTOs

#### Commands (input)

```text
<Action><Entity>Command
```

Examples:

```text
CreateUserCommand
CreateSchoolCommand
UpdateBranchCommand
EnrollStudentCommand
```

---

#### Results (output)

```text
<Entity>Result
```

Examples:

```text
UserResult
SchoolResult
BranchResult
EnrollmentResult
```

---

### 3.4 Mappers

```text
<Entity>Mapper
```

Examples:

```text
UserMapper
SchoolMapper
BranchMapper
```

---

### 3.5 Ports

#### Input Ports

```text
<Action><Entity>UseCase
```

(interfaces already covered as use cases)

---

#### Output Ports

```text
<Entity>Repository
<Entity>Gateway
```

Examples:

```text
UserRepository
SchoolRepository
EmailGateway
```

---

### 3.6 Policies (Authorization / Validation)

```text
<Resource>Policy
```

Examples:

```text
GroupPolicy
EnrollmentPolicy
MembershipPolicy
```

---

## 4. Infrastructure Layer

### 4.1 Persistence

```text
<Entity>Entity
<Entity>RepositoryImpl
```

Examples:

```text
UserEntity
UserRepositoryImpl
```

---

### 4.2 Adapters

```text
<Service>Adapter
```

Examples:

```text
EmailAdapter
JwtAdapter
```

---

## 5. Interfaces Layer (REST)

### 5.1 Controllers

```text
<Entity>Controller
```

Examples:

```text
UserController
SchoolController
BranchController
```

---

### 5.2 Requests

```text
<Action><Entity>Request
```

Examples:

```text
CreateUserRequest
UpdateSchoolRequest
```

---

### 5.3 Responses

```text
<Entity>Response
```

Examples:

```text
UserResponse
SchoolResponse
```

---

## 6. Method Naming

### 6.1 Factory Methods (Domain)

```text
create(...)
restore(...)
```

---

### 6.2 State Changes

Use semantic naming:

```text
changeData(...)
changeBasicInfo(...)
changeContactInfo(...)
activate()
deactivate()
```

---

### 6.3 Boolean Methods

Use `is` / `has`:

```text
isActive()
isMain()
hasAccess()
```

---

## 7. Package Naming

* Use lowercase.
* No underscores.
* Follow subdomain structure.

```text
domain.user.model
application.school.usecase
infrastructure.branch.persistence
interfaces.rest.membership
```

---

## 8. Anti-Patterns (Avoid)

❌ `UserService` (ambiguous)
❌ `UserManager`
❌ `DataHandler`
❌ `Utils` / `Helper`
❌ `CommonService`

👉 Always prefer **explicit names tied to business intent**

---

## 9. Summary

* Domain → business language only
* Application → explicit use cases
* DTOs → command / result
* Ports → repository / gateway
* Policies → explicit authorization rules

This ensures:

* clarity for developers
* consistency across modules
* better AI-assisted development
* long-term scalability

