# School Management Specification

## 1. Database Schema

**Requirement: Flyway V3**

The system MUST provide the schools table via Flyway migration V3.

| Table | PK | Key columns | Constraints |
|-------|----|-------------|-------------|
| `schools` | id | name, code, short_name, description, email, phone, address, status, created_at, updated_at | UNIQUE(code), UNIQUE(name) |

- GIVEN blank PostgreSQL WHEN V3 runs THEN schools table exists with schema and unique indexes

---

## 2. Create School

**Requirement: Create School**

POST /api/v1/schools MUST validate unique code and name, persist with ACTIVE status, require PLATFORM_ADMIN.

- GIVEN valid request with PLATFORM_ADMIN WHEN POST THEN 201 with SchoolResponse
- GIVEN duplicate code WHEN POST THEN 409
- GIVEN duplicate name WHEN POST THEN 409
- GIVEN non-PLATFORM_ADMIN WHEN POST THEN 403

---

## 3. Get School

**Requirement: Get School by ID**

GET /api/v1/schools/{id} MUST return school data or 404.

- GIVEN existing school WHEN GET THEN 200 with SchoolResponse
- GIVEN unknown id THEN 404

---

## 4. List Schools

**Requirement: List Schools**

GET /api/v1/schools MUST return all schools for PLATFORM_ADMIN or SCHOOL_ADMIN.

- GIVEN PLATFORM_ADMIN WHEN GET THEN 200 with school list
- GIVEN SCHOOL_ADMIN WHEN GET THEN 200 with school list
- GIVEN unauthenticated request THEN 401

---

## 5. Update School

**Requirement: Update School Data**

PUT /api/v1/schools/{id} MUST update school; reject if school is INACTIVE or user is not SCHOOL_ADMIN.

- GIVEN active school and SCHOOL_ADMIN WHEN PUT valid data THEN 200 with updated SchoolResponse
- GIVEN INACTIVE school WHEN PUT THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403

---

## 6. Deactivate School

**Requirement: Deactivate School**

PATCH /api/v1/schools/{id}/deactivate MUST set status to INACTIVE; reject if school has active branches.

- GIVEN school with no active branches and SCHOOL_ADMIN WHEN PATCH THEN 200
- GIVEN school with active branches WHEN PATCH THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403

---

## 7. Infrastructure

**Requirement: @Transactional**

Every mutable service method MUST be @Transactional; reads MUST use @Transactional(readOnly = true).

- GIVEN CreateSchoolService WHEN run THEN write is transactional
- GIVEN read operation WHEN called THEN readOnly=true is set
