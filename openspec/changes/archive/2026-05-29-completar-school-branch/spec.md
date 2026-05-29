# School & Branch Specification

## school-management

### 1. Database Schema

**Requirement: Flyway V3**

The system MUST provide the schools table via Flyway migration V3.

| Table | PK | Key columns | Constraints |
|-------|----|-------------|-------------|
| `schools` | id | name, code, short_name, description, email, phone, address, status, created_at, updated_at | UNIQUE(code), UNIQUE(name) |

- GIVEN blank PostgreSQL WHEN V3 runs THEN schools table exists with schema and unique indexes

---

### 2. Create School

**Requirement: Create School**

POST /api/v1/schools MUST validate unique code and name, persist with ACTIVE status, require PLATFORM_ADMIN.

- GIVEN valid request with PLATFORM_ADMIN WHEN POST THEN 201 with SchoolResponse
- GIVEN duplicate code WHEN POST THEN 409
- GIVEN duplicate name WHEN POST THEN 409
- GIVEN non-PLATFORM_ADMIN WHEN POST THEN 403

---

### 3. Get School

**Requirement: Get School by ID**

GET /api/v1/schools/{id} MUST return school data or 404.

- GIVEN existing school WHEN GET THEN 200 with SchoolResponse
- GIVEN unknown id THEN 404

---

### 4. List Schools

**Requirement: List Schools**

GET /api/v1/schools MUST return all schools for PLATFORM_ADMIN or SCHOOL_ADMIN.

- GIVEN PLATFORM_ADMIN WHEN GET THEN 200 with school list
- GIVEN SCHOOL_ADMIN WHEN GET THEN 200 with school list
- GIVEN unauthenticated request THEN 401

---

### 5. Update School

**Requirement: Update School Data**

PUT /api/v1/schools/{id} MUST update school; reject if school is INACTIVE or user is not SCHOOL_ADMIN.

- GIVEN active school and SCHOOL_ADMIN WHEN PUT valid data THEN 200 with updated SchoolResponse
- GIVEN INACTIVE school WHEN PUT THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403

---

### 6. Deactivate School

**Requirement: Deactivate School**

PATCH /api/v1/schools/{id}/deactivate MUST set status to INACTIVE; reject if school has active branches.

- GIVEN school with no active branches and SCHOOL_ADMIN WHEN PATCH THEN 200
- GIVEN school with active branches WHEN PATCH THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403

---

### 7. Infrastructure

**Requirement: @Transactional**

Every mutable service method MUST be @Transactional; reads MUST use @Transactional(readOnly = true).

- GIVEN CreateSchoolService WHEN run THEN write is transactional
- GIVEN read operation WHEN called THEN readOnly=true is set

---

## branch-management

### 1. Database Schema

**Requirement: Flyway V4**

The system MUST provide the branches table via Flyway V4 with FK to schools.

| Table | PK | Key columns | Constraints |
|-------|----|-------------|-------------|
| `branches` | id | school_id (FK→schools), name, code, short_name, description, email, phone, address, type, status, timestamps | FK(school_id), INDEX(school_id), UNIQUE(school_id, name) |

- GIVEN blank PostgreSQL WHEN V4 runs THEN branches table exists with FK and unique constraint

---

### 2. Create Branch

**Requirement: Create Branch**

POST /api/v1/schools/{schoolId}/branches MUST validate school is active, enforce MAIN uniqueness, validate type-address rules, require SCHOOL_ADMIN.

- GIVEN active school and valid MAIN branch with SCHOOL_ADMIN WHEN POST THEN 201 with BranchResponse
- GIVEN INACTIVE school WHEN POST THEN 422
- GIVEN second MAIN branch for same school WHEN POST THEN 409
- GIVEN VIRTUAL branch WITH address WHEN POST THEN 422
- GIVEN PHYSICAL branch WITHOUT address WHEN POST THEN 422
- GIVEN unknown schoolId WHEN POST THEN 404
- GIVEN non-SCHOOL_ADMIN WHEN POST THEN 403

---

### 3. Get Branch

**Requirement: Get Branch by ID**

GET /api/v1/schools/{schoolId}/branches/{id} MUST return branch scoped to parent school or 404.

- GIVEN existing branch under the school WHEN GET THEN 200 with BranchResponse
- GIVEN branch belongs to different school WHEN GET THEN 404
- GIVEN unknown branch id WHEN GET THEN 404

---

### 4. List Branches

**Requirement: List Branches by School**

GET /api/v1/schools/{schoolId}/branches MUST return all branches for the school.

- GIVEN school with 3 branches WHEN GET THEN 200 with 3 items
- GIVEN school with no branches WHEN GET THEN 200 with empty list
- GIVEN unknown schoolId WHEN GET THEN 404

---

### 5. Update Branch

**Requirement: Update Branch Info**

PUT /api/v1/schools/{schoolId}/branches/{id} MUST update branch; reject if branch is INACTIVE or user lacks SCHOOL_ADMIN/BRANCH_ADMIN.

- GIVEN active branch and SCHOOL_ADMIN WHEN PUT valid data THEN 200 with updated BranchResponse
- GIVEN INACTIVE branch WHEN PUT THEN 422
- GIVEN unauthorized role WHEN PUT THEN 403

---

### 6. Deactivate Branch

**Requirement: Deactivate Branch**

PATCH /api/v1/schools/{schoolId}/branches/{id}/deactivate MUST set status to INACTIVE; reject if MAIN branch has active secondary branches.

- GIVEN branch with no dependent branches and SCHOOL_ADMIN WHEN PATCH THEN 200
- GIVEN MAIN branch with active secondary branches WHEN PATCH THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403

---

### 7. Infrastructure

**Requirement: @Transactional**

Every mutable branch service method MUST be @Transactional.

- GIVEN CreateBranchService WHEN run THEN write is transactional
- GIVEN deactivate operation WHEN called THEN status change is atomic

### Requirement: Security Wiring

School and branch endpoints MUST require authentication; authorization uses role checks per endpoint.

- GIVEN unauthenticated request to any school or branch endpoint THEN 401
- GIVEN authenticated but unauthorized role THEN 403

---

### Dependencies

Both capabilities depend on:
- **Flyway** baseline for V3/V4 migrations
- **User Auth** for JWT authentication and role resolution
- **Membership Management** for SCHOOL_ADMIN and BRANCH_ADMIN role assignment within school scope
