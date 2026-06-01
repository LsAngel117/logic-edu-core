package com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "group_schedules")
public class GroupScheduleEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column
    private String classroom;

    /* ------------------ GETTERS ------------------ */

    public String getId() { return id; }
    public GroupEntity getGroup() { return group; }
    public String getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getClassroom() { return classroom; }

    /* ------------------ SETTERS ------------------ */

    public void setId(String id) { this.id = id; }
    public void setGroup(GroupEntity group) { this.group = group; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setClassroom(String classroom) { this.classroom = classroom; }
}
