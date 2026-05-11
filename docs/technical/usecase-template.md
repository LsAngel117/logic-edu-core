# Use Case Template

This document defines the standard structure for implementing application use cases.

---

## 1. Naming Convention

### Input Port

Use the suffix:

```text
UseCase
```

Example:

```text
CreateUserUseCase
```

---

### Application Implementation

Use the suffix:

```text
Service
```

Example:

```text
CreateUserService
```

---

## 2. Standard Structure

```text
application/{subdomain}/

  dto/
    command/
    result/

  mapper/

  port/
    in/
    out/

  policy/

  usecase/
```

---

## 3. Standard Flow

```text
Controller
  → Command
  → UseCase
  → Service
  → Domain
  → Repository
  → Mapper
  → Result
```

---

## 4. Responsibilities

### dto/command

Input data for the use case.

Example:

```text
CreateUserCommand
```

Contains no business logic.

---

### dto/result

Output data returned by the use case.

Example:

```text
UserResult
```

Must not expose domain entities directly.

---

### port/in

Defines the use case contract.

Example:

```text
CreateUserUseCase
```

---

### port/out

Defines external dependencies.

Examples:

```text
UserRepository
PasswordEncoderPort
ClockPort
```

---

### usecase

Implements the application logic.

Example:

```text
CreateUserService
```

Responsibilities:

* orchestrate domain operations
* coordinate repositories
* execute validations
* enforce authorization
* manage transactions

Must NOT contain:

* SQL logic
* HTTP logic
* framework-specific logic
* complex domain rules

---

### mapper

Transforms:

```text
Command → Domain
Domain → Result
```

---

### policy

Contains application-level validations and authorization rules.

Examples:

```text
UserAuthorizationPolicy
MembershipPolicy
```

---

## 5. Example Structure

```text
application/user/

  dto/
    command/
      CreateUserCommand.java

    result/
      UserResult.java

  mapper/
    UserMapper.java

  port/
    in/
      CreateUserUseCase.java

    out/
      UserRepository.java

  policy/
    UserAuthorizationPolicy.java

  usecase/
    CreateUserService.java
```

---

## 6. Design Principles

* Keep use cases focused on one action.
* Use explicit naming.
* Keep domain logic inside domain models/services.
* Keep application services thin and orchestrative.
* Prefer immutability.
* All operations must go through use cases.

---

## 7. Summary

* `UseCase` → contract
* `Service` → implementation
* Domain contains business rules
* Application orchestrates execution
* Infrastructure handles technical concerns

This standard ensures consistency, scalability, and maintainability across the entire system.
