package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.academic.subject.usecase.*;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class SubjectBeansConfig {
    @Bean public CreateSubjectService createSubjectService(SubjectRepository sr, SchoolRepository schR, Clock c) { return new CreateSubjectService(sr, schR, c); }
    @Bean public GetSubjectService getSubjectService(SubjectRepository r) { return new GetSubjectService(r); }
    @Bean public ListSubjectsBySchoolService listSubjectsBySchoolService(SubjectRepository r) { return new ListSubjectsBySchoolService(r); }
    @Bean public UpdateSubjectService updateSubjectService(SubjectRepository r, Clock c) { return new UpdateSubjectService(r, c); }
    @Bean public DeactivateSubjectService deactivateSubjectService(SubjectRepository r, Clock c) { return new DeactivateSubjectService(r, c); }
}
