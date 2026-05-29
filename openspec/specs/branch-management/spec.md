# Branch Management Specification

## 1. Database Schema

**Requirement: Flyway V4**

The system MUST provide the branches table via Flyway V4 with FK to schools.

| Table | PK | Key columns | Constraints |
|-------|----|-------------|-------------|
| `branches` | id | school_id (FK→schools), name, code, short_name, description, email, phone, address, type, status, timestamps | FK(school_id), INDEX(school_id), UNIQUE(school_id, name) |

- GIVEN blank PostgreSQL WHEN V4 runs THEN branches table exists with FK and unique constraint

---

## 2. Create Branch

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

## 3. Get Branch

**Requirement: Get Branch by ID**

GET /api/v1/schools/{schoolId}/branches/{id} MUST return branch scoped to parent school or 404.

- GIVEN existing branch under the school WHEN GET THEN 200 with BranchResponse
- GIVEN branch belongs to different school WHEN GET THEN 404
- GIVEN unknown branch id WHEN GET THEN 404

---

## 4. List Branches

**Requirement: List Branches by School**

GET /api/v1/schools/{schoolId}/branches MUST return all branches for the school.

- GIVEN school with 3 branches WHEN GET THEN 200 with 3 items
- GIVEN school with no branches WHEN GET THEN 200 with empty list
- GIVEN unknown schoolId WHEN GET THEN 404

---

## 5. Update Branch

**Requirement: Update Branch Info**

PUT /api/v1/schools/{schoolId}/branches/{id} MUST update branch; reject if branch is INACTIVE or user lacks SCHOOL_ADMIN/BRANCH_ADMIN.

- GIVEN active branch and SCHOOL_ADMIN WHEN PUT valid data THEN 200 with updated BranchResponse
- GIVEN INACTIVE branch WHEN PUT THEN 422
- GIVEN unauthorized role WHEN PUT THEN 403

---

## 6. Deactivate Branch

**Requirement: Deactivate Branch**

PATCH /api/v1/schools/{schoolId}/branches/{id}/deactivate MUST set status to INACTIVE; reject if MAIN branch has active secondary branches.

- GIVEN branch with no dependent branches and SCHOOL_ADMIN WHEN PATCH THEN 200
- GIVEN MAIN branch with active secondary branches WHEN PATCH THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403

---

## 7. Infrastructure

**Requirement: @Transactional**

Every mutable branch service method MUST be @Transactional.

- GIVEN CreateBranchService WHEN run THEN write is transactional
- GIVEN deactivate operation WHEN called THEN status change is atomic

## 8. Security Wiring

**Requirement: Security Wiring**

School and branch endpoints MUST require authentication; authorization uses role checks per endpoint.

- GIVEN unauthenticated request to any school or branch endpoint THEN 401
- GIVEN authenticated but unauthorized role THEN 403

---

## 9. Dependencies

Both capabilities depend on:
- **Flyway** baseline for V3/V4 migrations
- **User Auth** for JWT authentication and role resolution
- **Membership Management** for SCHOOL_ADMIN and BRANCH_ADMIN role assignment within school scope
