package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.attendance.port.out.AttendanceRepository;
import com.logossystemsit.logiceducore.application.academic.attendance.usecase.*;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class AttendanceBeansConfig {
    @Bean public RegisterAttendanceService registerAttendanceService(AttendanceRepository ar, GroupRepository gr, Clock c) { return new RegisterAttendanceService(ar, gr, c); }
    @Bean public GetAttendanceByDateService getAttendanceByDateService(AttendanceRepository r) { return new GetAttendanceByDateService(r); }
    @Bean public ListAttendancesByGroupService listAttendancesByGroupService(AttendanceRepository r) { return new ListAttendancesByGroupService(r); }
    @Bean public UpdateAttendanceService updateAttendanceService(AttendanceRepository ar, GroupRepository gr, Clock c) { return new UpdateAttendanceService(ar, gr, c); }
}
