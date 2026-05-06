# Business Rules

## 1. Core Rules

- Every entity must have a unique identifier.
- All aggregates must enforce their own invariants.
- Domain models are immutable; changes create new instances.
- No entity can be modified if it is inactive.

---

## 2. Identity & User

- A user must have a unique username and email.
- Username generation must follow system-defined rules and normalization.
- Username must be unique across the platform.
- Document type must be consistent with age:
  - TI → only minors
  - CC → only adults
- A user must always have at least one active membership to operate in the system.
- A user can have multiple memberships across scopes.

---

## 3. Membership & Authorization

- A membership defines the role of a user within a specific scope.
- A role can only be assigned to supported scope types.
- Scope types:
  - PLATFORM
  - SCHOOL
  - BRANCH
  - COURSE
- A membership must always be associated with a valid and active scope.
- A membership cannot be assigned to an inactive entity.
- A user may have multiple memberships but must not have conflicting roles within the same scope.

### Authorization rules (high level)

- A user can only operate within the scope of their memberships.
- A SCHOOL_ADMIN can operate across all branches of their school.
- A BRANCH_ADMIN can operate only within their branch.
- A TEACHER can operate only within assigned groups.
- A STUDENT can only access their own academic data.

---

## 4. School & Branch

- A school can have multiple branches.
- A branch belongs to exactly one school.
- A branch cannot exist without a school.
- A school must have at least one main branch.
- Only one branch can be marked as MAIN per school.
- An inactive school cannot accept new branches.
- An inactive branch cannot be modified or assigned new operations.

---

## 5. Academic Structure

- Each school defines its own academic structure.
- Academic structure must be versioned and cannot break historical data.
- A structure defines:
  - number of levels
  - periods per level
  - subjects per period
  - hours per subject
  - whether evaluation sub-periods exist
- Not all structures require evaluation periods (cortes).

---

## 6. Academic Periods

- An academic period belongs to a level.
- A level belongs to a school.
- Period types may vary (semester, trimester, cycle).
- Periods must not overlap within the same level.
- Evaluation periods (if defined) must belong to an academic period.

---

## 7. Subjects & Groups

- A subject represents an academic unit of knowledge.
- A group represents the offering of a subject in a specific period and branch.
- A group must be linked to:
  - subject
  - academic period
  - branch
- A group must have a defined capacity.
- A group must have an assigned teacher to operate.

---

## 8. Enrollment

- A student cannot be enrolled in the same group more than once.
- A student cannot be enrolled in two groups of the same subject within the same period.
- Enrollment must reference an active group.
- Enrollment must reference an active student.

---

## 9. Schedule

- A group must have a valid schedule to operate.
- Schedule entries must not overlap within the same group.
- A teacher cannot have overlapping schedules.

---

## 10. Attendance

- Only one attendance record per student per session.
- Attendance cannot be registered outside the valid session time.
- Only authorized users (teacher or admin) can register attendance.

---

## 11. Assessment & Grading

- Assessments must belong to a group.
- Assessments may belong to an evaluation period (if defined).
- Grades must be associated with:
  - student
  - group
  - assessment
- Final grade must be computed based on defined rules (weights or averages).
- Only the assigned teacher can register or modify grades.

---

## 12. Audit & Integrity

- Historical academic data must never be overwritten.
- Changes to critical entities must preserve traceability.
- Deactivation is preferred over deletion.
