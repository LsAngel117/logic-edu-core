package com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
public class GroupEntity {

    @Id
    private String id;

    @Column(name = "school_id", nullable = false)
    private String schoolId;

    @Column(name = "subject_id", nullable = false)
    private String subjectId;

    @Column(name = "academic_period_id", nullable = false)
    private String academicPeriodId;

    @Column(name = "branch_id", nullable = false)
    private String branchId;

    @Column(name = "teacher_id", nullable = false)
    private String teacherId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String status;

    @Version
    private Long version;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupScheduleEntity> schedules = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getSchoolId() { return schoolId; }
    public String getSubjectId() { return subjectId; }
    public String getAcademicPeriodId() { return academicPeriodId; }
    public String getBranchId() { return branchId; }
    public String getTeacherId() { return teacherId; }
    public String getCode() { return code; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }
    public Long getVersion() { return version; }
    public List<GroupScheduleEntity> getSchedules() { return schedules; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public void setAcademicPeriodId(String academicPeriodId) { this.academicPeriodId = academicPeriodId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public void setCode(String code) { this.code = code; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setStatus(String status) { this.status = status; }
    public void setVersion(Long version) { this.version = version; }
    public void setSchedules(List<GroupScheduleEntity> schedules) { this.schedules = schedules; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
