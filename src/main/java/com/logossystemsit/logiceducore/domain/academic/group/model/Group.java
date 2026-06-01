package com.logossystemsit.logiceducore.domain.academic.group.model;

import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Group {

    private final GroupId id;
    private final SchoolId schoolId;
    private final SubjectId subjectId;
    private final AcademicPeriodId academicPeriodId;
    private final BranchId branchId;
    private final UserId teacherId;
    private final String code;
    private final int capacity;
    private final GroupStatus status;
    private final Long version;
    private final List<Schedule> schedules;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Group(
            GroupId id,
            SchoolId schoolId,
            SubjectId subjectId,
            AcademicPeriodId academicPeriodId,
            BranchId branchId,
            UserId teacherId,
            String code,
            int capacity,
            GroupStatus status,
            Long version,
            List<Schedule> schedules,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.schoolId = Objects.requireNonNull(schoolId, "schoolId is required");
        this.subjectId = Objects.requireNonNull(subjectId, "subjectId is required");
        this.academicPeriodId = Objects.requireNonNull(academicPeriodId, "academicPeriodId is required");
        this.branchId = Objects.requireNonNull(branchId, "branchId is required");
        this.teacherId = Objects.requireNonNull(teacherId, "teacherId is required");

        Objects.requireNonNull(code, "code is required");
        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        this.code = code.trim();

        this.capacity = capacity;
        this.status = Objects.requireNonNull(status, "status is required");
        this.version = version;
        this.schedules = Collections.unmodifiableList(
                new ArrayList<>(Objects.requireNonNull(schedules, "schedules is required"))
        );
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        validate();
    }

    /* ---------- FACTORY METHODS ---------- */

    public static Group create(
            GroupId id,
            SchoolId schoolId,
            SubjectId subjectId,
            AcademicPeriodId academicPeriodId,
            BranchId branchId,
            UserId teacherId,
            String code,
            int capacity,
            List<Schedule> schedules,
            Instant now
    ) {
        return new Group(
                id, schoolId, subjectId, academicPeriodId, branchId, teacherId,
                code, capacity,
                GroupStatus.ACTIVE,
                0L,
                schedules,
                now, now
        );
    }

    public static Group restore(
            GroupId id,
            SchoolId schoolId,
            SubjectId subjectId,
            AcademicPeriodId academicPeriodId,
            BranchId branchId,
            UserId teacherId,
            String code,
            int capacity,
            List<Schedule> schedules,
            GroupStatus status,
            Long version,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Group(
                id, schoolId, subjectId, academicPeriodId, branchId, teacherId,
                code, capacity,
                status,
                version,
                schedules,
                createdAt, updatedAt
        );
    }

    /* ---------- BEHAVIOR ---------- */

    public Group changeData(
            AcademicPeriodId newPeriodId,
            BranchId newBranchId,
            UserId newTeacherId,
            String newCode,
            int newCapacity,
            Instant now
    ) {
        Objects.requireNonNull(newPeriodId, "academicPeriodId is required");
        Objects.requireNonNull(newBranchId, "branchId is required");
        Objects.requireNonNull(newTeacherId, "teacherId is required");
        Objects.requireNonNull(newCode, "code is required");
        if (newCode.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (newCapacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }

        return new Group(
                this.id, this.schoolId, this.subjectId,
                newPeriodId, newBranchId, newTeacherId,
                newCode.trim(), newCapacity,
                this.status, this.version,
                this.schedules,
                this.createdAt, now
        );
    }

    public Group changeSchedules(List<Schedule> newSchedules, Instant now) {
        Objects.requireNonNull(newSchedules, "schedules is required");

        return new Group(
                this.id, this.schoolId, this.subjectId,
                this.academicPeriodId, this.branchId, this.teacherId,
                this.code, this.capacity,
                this.status, this.version,
                newSchedules,
                this.createdAt, now
        );
    }

    public Group deactivate(Instant now) {
        if (this.status == GroupStatus.INACTIVE) {
            return this;
        }

        return new Group(
                this.id, this.schoolId, this.subjectId,
                this.academicPeriodId, this.branchId, this.teacherId,
                this.code, this.capacity,
                GroupStatus.INACTIVE, this.version,
                this.schedules,
                this.createdAt, now
        );
    }

    /* ---------- VALIDATION ---------- */

    private void validate() {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }
    }

    /* ---------- EQUALS / HASHCODE ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group group)) return false;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /* ---------- GETTERS ---------- */

    public GroupId getId() { return id; }
    public SchoolId getSchoolId() { return schoolId; }
    public SubjectId getSubjectId() { return subjectId; }
    public AcademicPeriodId getAcademicPeriodId() { return academicPeriodId; }
    public BranchId getBranchId() { return branchId; }
    public UserId getTeacherId() { return teacherId; }
    public String getCode() { return code; }
    public int getCapacity() { return capacity; }
    public GroupStatus getStatus() { return status; }
    public Long getVersion() { return version; }
    public List<Schedule> getSchedules() { return schedules; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
