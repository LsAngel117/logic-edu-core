package com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "attendances",
       uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "date", "student_id"}))
public class AttendanceEntity {

    @Id
    private String id;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String status;

    @Column(nullable = true)
    private String observations;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public String getGroupId() { return groupId; }
    public String getStudentId() { return studentId; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public String getObservations() { return observations; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
    public void setObservations(String observations) { this.observations = observations; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
