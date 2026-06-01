package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.assessment.port.out.AssessmentRepository;
import com.logossystemsit.logiceducore.application.academic.assessment.usecase.*;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.grade.port.out.GradeRepository;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class AssessmentBeansConfig {
    @Bean public CreateAssessmentService createAssessmentService(AssessmentRepository ar, GroupRepository gr, EvaluationPeriodRepository er, Clock c) { return new CreateAssessmentService(ar, gr, er, c); }
    @Bean public GetAssessmentService getAssessmentService(AssessmentRepository r) { return new GetAssessmentService(r); }
    @Bean public ListAssessmentsByGroupService listAssessmentsByGroupService(AssessmentRepository r) { return new ListAssessmentsByGroupService(r); }
    @Bean public UpdateAssessmentService updateAssessmentService(AssessmentRepository ar, GroupRepository gr, Clock c) { return new UpdateAssessmentService(ar, gr, c); }
    @Bean public DeleteAssessmentService deleteAssessmentService(AssessmentRepository ar, GroupRepository gr, GradeRepository graR, Clock c) { return new DeleteAssessmentService(ar, gr, graR, c); }
}
