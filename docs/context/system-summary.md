# System Summary — Edu Core Platform

## 1. Overview

Edu Core Platform is a multi-institution academic management system designed to handle:

- student attendance tracking
- academic progress monitoring
- academic structure management (courses, groups, schedules)
- user and role management
- notifications and alerts

The system replaces manual processes with a centralized, scalable and secure digital platform.

---

## 2. Core Principles

- Domain-driven design mindset
- Hexagonal Architecture (Ports and Adapters)
- Clean separation of concerns
- Backend as single source of truth
- Multi-tenant from day one

---

## 3. Architecture

### Style
Modular Monolith using Hexagonal Architecture

### Layers

- **Domain**
    - Pure business logic
    - No framework dependencies

- **Application**
    - Use cases
    - Orchestrates domain logic

- **Infrastructure**
    - Persistence (JPA, PostgreSQL)
    - Security (JWT)
    - External services

- **Interfaces**
    - REST controllers (Spring Boot)

### Dependency Rule

- Dependencies point inward
- Domain does not depend on any external layer

---

## 4. Technology Stack

### Backend
- Java (LTS version, e.g., 25)
- Spring Boot 4.0.6
- Gradle (Kotlin DSL)

### Database
- PostgreSQL 18.3
- Flyway for migrations
- JPA (Hibernate)

### Security
- Spring Security
- JWT authentication

### Clients
- Web: Angular 21
- Mobile: Kotlin (Android)

### API
- REST
- JSON

---

## 5. Core Domain Concepts

### Users
- Admin
- Teacher
- Student

### Academic Structure
- Institution
- Course
- Group
- Schedule

### Academic Flow
- Enrollment
- AttendanceSession
- AttendanceRecord
- ProgressRecord

---

## 6. Key Business Rules

- Every entity belongs to an institution (multi-tenancy)
- A student cannot be enrolled in multiple groups of the same course
- A teacher can only manage their assigned groups
- Only one attendance record per student per session
- Attendance must be registered within valid schedule constraints

---

## 7. Multi-Tenancy Strategy

- Single database
- Logical isolation using `institution_id`
- All queries must enforce tenant filtering

---

## 8. API Design

- RESTful endpoints
- Stateless communication
- JWT-based authentication
- Same API used by web and mobile clients

---

## 9. Security

- Authentication via JWT
- Role-based authorization
- Backend validation is mandatory
- Sensitive data protection enforced

---

## 10. Scalability Approach

### Current
- Modular monolith

### Future
- Possible extraction to microservices if needed

### Design considerations
- Clear module boundaries
- Decoupled domain
- Event-driven expansion possible

---

## 11. Development Guidelines

- No framework annotations in domain layer
- No business logic in controllers
- Use cases must encapsulate application logic
- Persistence models must be separated from domain models
- All changes to DB must go through migrations

---

## 12. Project Structure (Conceptual)
- domain/
- application/
- infrastructure/
- interfaces/
### Structure Principles

- The **domain layer is completely framework-independent**
- The **application layer orchestrates use cases without business leakage**
- The **infrastructure layer implements technical concerns (JPA, security, etc.)**
- The **interfaces layer exposes the system via REST**

### Critical Rules

- No Spring annotations in domain
- No business logic in controllers
- No direct access from controllers to repositories
- All external interactions go through ports
---

## 13. Current Status

- Architecture defined
- Core domain identified
- Documentation structured
- Initial implementation phase starting

---

## 14. Purpose of this File

This document provides a concise but complete understanding of the system for:

- developers
- architects
- AI assistants

It should be used as the primary context input when interacting with AI tools.