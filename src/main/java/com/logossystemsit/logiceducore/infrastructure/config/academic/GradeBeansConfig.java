package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.application.academic.grade.usecase.*;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class GradeBeansConfig {
    @Bean public RegisterGradeService registerGradeService(GradeRepository gr, AssessmentRepository ar, GroupRepository gpr, UserRepository ur, Clock c) { return new RegisterGradeService(gr, ar, gpr, ur, c); }
    @Bean public GetGradeService getGradeService(GradeRepository r) { return new GetGradeService(r); }
    @Bean public ListGradesByAssessmentService listGradesByAssessmentService(GradeRepository r) { return new ListGradesByAssessmentService(r); }
    @Bean public UpdateGradeService updateGradeService(GradeRepository gr, AssessmentRepository ar, GroupRepository gpr, Clock c) { return new UpdateGradeService(gr, ar, gpr, c); }
}
