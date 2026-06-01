package com.logossystemsit.logiceducore.application.academic.attendance.usecase;

import com.logossystemsit.logiceducore.application.academic.attendance.dto.result.AttendanceResult;
import com.logossystemsit.logiceducore.application.academic.attendance.port.in.ListAttendancesByGroupUseCase;
import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListAttendancesByGroupService implements ListAttendancesByGroupUseCase {

    private final AttendanceRepository attendanceRepository;

    public ListAttendancesByGroupService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResult> execute(GroupId groupId) {
        return attendanceRepository.findByGroupId(groupId).stream()
                .map(AttendanceResult::from)
                .toList();
    }
}
