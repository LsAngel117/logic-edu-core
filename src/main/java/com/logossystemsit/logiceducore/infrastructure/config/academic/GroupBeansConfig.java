package com.logossystemsit.logiceducore.infrastructure.config.academic;

import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.academic.group.usecase.*;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class GroupBeansConfig {
    @Bean public CreateGroupService createGroupService(GroupRepository gr, SchoolRepository schR, SubjectRepository sr, AcademicPeriodRepository pr, BranchRepository br, MembershipRepository mr, Clock c) { return new CreateGroupService(gr, schR, sr, pr, br, mr, c); }
    @Bean public GetGroupService getGroupService(GroupRepository r) { return new GetGroupService(r); }
    @Bean public ListGroupsBySchoolService listGroupsBySchoolService(GroupRepository r) { return new ListGroupsBySchoolService(r); }
    @Bean public UpdateGroupService updateGroupService(GroupRepository gr, SchoolRepository schR, SubjectRepository sr, AcademicPeriodRepository pr, BranchRepository br, MembershipRepository mr, Clock c) { return new UpdateGroupService(gr, schR, sr, pr, br, mr, c); }
    @Bean public UpdateGroupSchedulesService updateGroupSchedulesService(GroupRepository r, Clock c) { return new UpdateGroupSchedulesService(r, c); }
    @Bean public DeactivateGroupService deactivateGroupService(GroupRepository r, Clock c) { return new DeactivateGroupService(r, c); }
}
