package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.application.academic.enrollment.usecase.*;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class EnrollmentBeansConfig {
    @Bean public EnrollStudentService enrollStudentService(EnrollmentRepository er, GroupRepository gr, UserRepository ur, Clock c) { return new EnrollStudentService(er, gr, ur, c); }
    @Bean public GetEnrollmentService getEnrollmentService(EnrollmentRepository r) { return new GetEnrollmentService(r); }
    @Bean public ListEnrollmentsByGroupService listEnrollmentsByGroupService(EnrollmentRepository r) { return new ListEnrollmentsByGroupService(r); }
    @Bean public DropEnrollmentService dropEnrollmentService(EnrollmentRepository r, Clock c) { return new DropEnrollmentService(r, c); }
}
