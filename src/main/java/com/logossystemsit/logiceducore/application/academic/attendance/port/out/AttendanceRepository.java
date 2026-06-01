package com.logossystemsit.logiceducore.application.academic.attendance.port.out;

import com.logossystemsit.logiceducore.domain.academic.attendance.model.Attendance;
import com.logossystemsit.logiceducore.domain.academic.attendance.model.AttendanceId;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository {

    void save(Attendance attendance);

    Optional<Attendance> findById(AttendanceId id);

    List<Attendance> findByGroupId(GroupId groupId);

    List<Attendance> findByGroupIdAndDate(GroupId groupId, LocalDate date);

    Optional<Attendance> findByGroupIdAndDateAndStudentId(GroupId groupId, LocalDate date, UserId studentId);
}
