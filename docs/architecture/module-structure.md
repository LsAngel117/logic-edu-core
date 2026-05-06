# Module Structure

The system is organized as a **modular monolith** using:

* Domain-Driven Design (DDD)
* Hexagonal Architecture (Ports & Adapters)

The structure is defined **by subdomain first**, then by architectural layers.

---

## 1. High-Level Structure

```
domain/
application/
infrastructure/
interfaces/
```

Each layer is divided into subdomains:

```
domain/
  user/
  school/
  branch/
  membership/
  academic/

application/
  user/
  school/
  branch/
  membership/
  academic/

infrastructure/
  user/
  school/
  branch/
  membership/
  academic/

interfaces/
  rest/
    user/
    school/
    branch/
    membership/
    academic/
```

---

## 2. Subdomains

The system is divided into the following subdomains:

* **user** → identity and personal data
* **membership** → roles, scopes, and access model
* **school** → institution management
* **branch** → school locations (sede)
* **academic** → academic structure and operations

---

## 3. Domain Layer

The domain layer contains the **core business logic**.

### Structure

```
domain/{subdomain}/
  model/
    valueobject/
  service/
```

### Responsibilities

* Entities and Aggregates
* Value Objects
* Domain Services
* Business Rules
* Invariants

### Example

```
domain/
  user/
    model/
      valueobject/
    service/

  school/
    model/
      valueobject/

  branch/
    model/
      valueobject/

  membership/
    model/
      valueobject/

  academic/
    structure/
    level/
    period/
    evaluation/
    subject/
    group/
    enrollment/
    schedule/
    attendance/
    assessment/
    grading/
    curriculum/
```

---

## 4. Application Layer

Coordinates use cases and orchestrates domain logic.

### Structure

```
application/{subdomain}/
  dto/
    command/
    result/
  mapper/
  port/
    in/
    out/
  usecase/
  policy/
```

### Responsibilities

* Use case execution
* Transaction management
* Cross-aggregate validation
* Authorization enforcement
* Coordination with repositories

---

## 5. Infrastructure Layer

Implements technical details and external integrations.

### Structure

```
infrastructure/{subdomain}/
  persistence/
  repository/
  adapter/
```

### Responsibilities

* Database access
* ORM (JPA, etc.)
* External APIs
* Messaging systems
* Implementing output ports

---

## 6. Interfaces Layer

Entry points to the system.

### Structure

```
interfaces/
  rest/
    {subdomain}/
```

### Responsibilities

* REST controllers
* Request/Response mapping
* Input validation
* Delegation to application layer

---

## 7. Dependency Rule

```
interfaces → application → domain  
infrastructure → application → domain  
```

* Domain is independent
* Application depends on domain
* Infrastructure implements application ports

---

## 8. Subdomain Isolation

* Subdomains must be independent
* No direct domain-to-domain coupling
* Communication via:

  * application layer
  * ports

---

## 9. Domain Principles

* No framework dependencies
* No persistence logic
* Immutable models
* Behavior inside aggregates
* Explicit invariants

---

## 10. Academic Subdomain

### Domain structure

```
domain/academic/
  structure/
  level/
  period/
  evaluation/
  subject/
  group/
  enrollment/
  schedule/
  attendance/
  assessment/
  grading/
  curriculum/
```

### Application structure

```
application/academic/
  dto/
  mapper/
  port/
  usecase/
  policy/
```

---

## 11. Membership & Authorization

* Domain defines:

  * Role
  * Scope
  * Membership rules

* Application enforces:

  * Authorization policies
  * Access control

---

## 12. Evolution Strategy

This architecture supports:

* Multi-tenancy
* Scalability by subdomain
* Future migration to microservices

Each subdomain can be extracted independently.

---

## 13. Naming Conventions

* Subdomains use singular names: `user`, `school`, `branch`
* Domain uses business language
* Avoid technical suffixes in domain (no "Entity", "DTO")

---

## 14. Summary

* Organized by subdomain first, then by layer
* Domain is the core
* Application orchestrates
* Infrastructure implements
* Interfaces expose

This structure ensures a **clean, scalable, and maintainable system** aligned with DDD and Hexagonal Architecture.
