package com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.Attendance;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.AttendanceId;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.AttendanceStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.entity.AttendanceEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.repository.AttendanceJpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AttendanceRepositoryAdapter implements AttendanceRepository {

    private final AttendanceJpaRepository jpa;

    public AttendanceRepositoryAdapter(AttendanceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Attendance attendance) {
        AttendanceEntity entity = mapToEntity(attendance);
        jpa.save(entity);
    }

    @Override
    public Optional<Attendance> findById(AttendanceId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<Attendance> findByGroupId(GroupId groupId) {
        return jpa.findByGroupId(groupId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Attendance> findByGroupIdAndDate(GroupId groupId, LocalDate date) {
        return jpa.findByGroupIdAndDate(groupId.value(), date).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public Optional<Attendance> findByGroupIdAndDateAndStudentId(
            GroupId groupId, LocalDate date, UserId studentId) {
        return jpa.findByGroupIdAndDateAndStudentId(groupId.value(), date, studentId.value())
                .map(this::mapToDomain);
    }

    private AttendanceEntity mapToEntity(Attendance attendance) {
        AttendanceEntity entity = new AttendanceEntity();
        entity.setId(attendance.getId().value());
        entity.setGroupId(attendance.getGroupId().value());
        entity.setStudentId(attendance.getStudentId().value());
        entity.setDate(attendance.getDate());
        entity.setStatus(attendance.getStatus().name());
        entity.setObservations(attendance.getObservations());
        entity.setCreatedAt(attendance.getCreatedAt());
        entity.setUpdatedAt(attendance.getUpdatedAt());
        return entity;
    }

    private Attendance mapToDomain(AttendanceEntity entity) {
        return Attendance.restore(
                new AttendanceId(entity.getId()),
                new GroupId(entity.getGroupId()),
                new UserId(entity.getStudentId()),
                entity.getDate(),
                AttendanceStatus.valueOf(entity.getStatus()),
                entity.getObservations(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
