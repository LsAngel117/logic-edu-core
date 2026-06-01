package com.logossystemsit.logiceducore.application.academic.attendance.usecase;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.application.academic.attendance.port.in.GetAttendanceByDateUseCase;
import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public class GetAttendanceByDateService implements GetAttendanceByDateUseCase {

    private final AttendanceRepository attendanceRepository;

    public GetAttendanceByDateService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResult> execute(GroupId groupId, LocalDate date) {
        return attendanceRepository.findByGroupIdAndDate(groupId, date).stream()
                .map(AttendanceResult::from)
                .toList();
    }
}
