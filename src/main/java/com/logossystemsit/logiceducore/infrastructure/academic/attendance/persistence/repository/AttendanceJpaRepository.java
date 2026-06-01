package com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.repository;

import com.logossystemsit.logiceducore.infrastructure.academic.attendance.persistence.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceJpaRepository extends JpaRepository<AttendanceEntity, String> {

    List<AttendanceEntity> findByGroupId(String groupId);

    List<AttendanceEntity> findByGroupIdAndDate(String groupId, LocalDate date);

    Optional<AttendanceEntity> findByGroupIdAndDateAndStudentId(String groupId, LocalDate date, String studentId);
}
