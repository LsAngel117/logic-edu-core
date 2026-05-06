# Authorization Policies

## 1. General Rules

- A user can only operate within their memberships.
- Access is always evaluated against:
  - role
  - scope
  - target resource
- If no valid membership exists → access is denied.

---

## 2. Scope-Based Access

### PLATFORM

- PLATFORM_ADMIN has full access across the system.

---

### SCHOOL

- SCHOOL_ADMIN can:
  - manage school data
  - manage branches within the school
  - manage academic structure
  - manage users within the school

- SCHOOL_ADMIN cannot:
  - access other schools

---

### BRANCH

- BRANCH_ADMIN can:
  - manage branch data
  - manage groups within the branch
  - manage schedules and operations

- BRANCH_ADMIN cannot:
  - operate outside their branch

---

### COURSE / GROUP

- TEACHER can:
  - manage assigned groups
  - register attendance
  - register grades
  - manage assessments

- TEACHER cannot:
  - access groups not assigned

---

### STUDENT

- STUDENT can:
  - view own enrollments
  - view own grades
  - view own attendance

- STUDENT cannot:
  - access other students' data
  - modify academic data

---

## 3. Cross-Scope Rules

- SCHOOL_ADMIN can operate across all branches within their school.
- BRANCH_ADMIN is limited strictly to their branch.
- TEACHER access is restricted to assigned groups only.
- STUDENT access is always self-scoped.

---

## 4. Resource-Based Rules

### School

- Only PLATFORM_ADMIN and SCHOOL_ADMIN can modify.

### Branch

- SCHOOL_ADMIN can create and manage.
- BRANCH_ADMIN can manage within their branch.

### Group

- SCHOOL_ADMIN and BRANCH_ADMIN can create.
- TEACHER can manage only assigned groups.

### Enrollment

- Only admins can create.
- Students cannot self-enroll unless explicitly allowed.

### Attendance

- Only TEACHER or authorized admin.

### Grades

- Only TEACHER of the group.

---

## 5. Validation Requirements

Every use case must validate:

- user is authenticated
- user has valid membership
- membership matches required role
- membership scope matches resource

---

## 6. Forbidden Scenarios

- Accessing resources outside scope
- Acting without membership
- Acting on inactive entities
- Assigning roles to invalid scopes

---

## 7. Future Extensions

The model supports:

- fine-grained permissions (RBAC → ABAC evolution)
- custom roles per school
- feature-based permissions
