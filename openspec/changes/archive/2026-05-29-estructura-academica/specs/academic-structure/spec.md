# Academic Structure Specification

## 1. Database Schema

**Requirement: Flyway V5**

The system MUST provide `academic_structures` via Flyway V5.

| Table | PK | Key Columns | Constraints |
|-------|----|-------------|-------------|
| `academic_structures` | id | school_id, structure_type, levels_count, periods_per_level, evaluation_periods_per_period, subjects_per_period, hours_per_subject, active, version | UNIQUE(school_id, version) |

- GIVEN blank PostgreSQL WHEN V5 runs THEN academic_structures table exists with schema and unique index

---

## 2. Create Structure

**Requirement: Create School Academic Structure**

POST /api/v1/schools/{schoolId}/structures MUST create the first active structure. Only one active structure per school is allowed. Requires SCHOOL_ADMIN.

- GIVEN valid request with SCHOOL_ADMIN WHEN POST THEN 201 with StructureResponse
- GIVEN existing active structure for same school WHEN POST THEN 409
- GIVEN non-SCHOOL_ADMIN WHEN POST THEN 403
- GIVEN unauthenticated request WHEN POST THEN 401

---

## 3. Get Structure

**Requirement: Get Active Structure by School**

GET /api/v1/schools/{schoolId}/structures/active MUST return the active structure for the school.

- GIVEN school with active structure WHEN GET THEN 200
- GIVEN school with no active structure WHEN GET THEN 404
- GIVEN unknown schoolId WHEN GET THEN 404

---

## 4. Update Structure

**Requirement: Versioned Structure Update**

PUT /api/v1/schools/{schoolId}/structures MUST deactivate current active structure and create a new version.

- GIVEN active structure with SCHOOL_ADMIN WHEN PUT valid data THEN 200 with new version
- GIVEN deactivated structure WHEN PUT THEN 422
- GIVEN non-SCHOOL_ADMIN WHEN PUT THEN 403

---

## 5. Deactivate Structure

**Requirement: Deactivate Structure**

PATCH /api/v1/schools/{schoolId}/structures/{id}/deactivate MUST set active=false.

- GIVEN active structure with SCHOOL_ADMIN WHEN PATCH THEN 200
- GIVEN already inactive structure WHEN PATCH THEN 409
- GIVEN non-SCHOOL_ADMIN WHEN PATCH THEN 403
