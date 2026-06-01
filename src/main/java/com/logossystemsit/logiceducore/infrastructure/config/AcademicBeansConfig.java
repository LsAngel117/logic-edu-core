package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.evaluation.usecase.*;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.application.academic.level.usecase.*;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.period.usecase.*;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.application.academic.structure.usecase.*;
import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.academic.group.usecase.*;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.academic.subject.usecase.*;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AcademicBeansConfig {

    @Bean
    public CreateAcademicStructureService createAcademicStructureService(
            AcademicStructureRepository repository,
            Clock clock) {
        return new CreateAcademicStructureService(repository, clock);
    }

    @Bean
    public GetAcademicStructureService getAcademicStructureService(
            AcademicStructureRepository repository) {
        return new GetAcademicStructureService(repository);
    }

    @Bean
    public UpdateAcademicStructureService updateAcademicStructureService(
            AcademicStructureRepository repository,
            Clock clock) {
        return new UpdateAcademicStructureService(repository, clock);
    }

    @Bean
    public DeactivateAcademicStructureService deactivateAcademicStructureService(
            AcademicStructureRepository repository,
            Clock clock) {
        return new DeactivateAcademicStructureService(repository, clock);
    }

    // ---- AcademicLevel Use Cases ----

    @Bean
    public CreateAcademicLevelService createAcademicLevelService(
            AcademicLevelRepository repository,
            Clock clock) {
        return new CreateAcademicLevelService(repository, clock);
    }

    @Bean
    public GetAcademicLevelService getAcademicLevelService(
            AcademicLevelRepository repository) {
        return new GetAcademicLevelService(repository);
    }

    @Bean
    public ListAcademicLevelsBySchoolService listAcademicLevelsBySchoolService(
            AcademicLevelRepository repository) {
        return new ListAcademicLevelsBySchoolService(repository);
    }

    @Bean
    public UpdateAcademicLevelService updateAcademicLevelService(
            AcademicLevelRepository repository,
            Clock clock) {
        return new UpdateAcademicLevelService(repository, clock);
    }

    @Bean
    public DeactivateAcademicLevelService deactivateAcademicLevelService(
            AcademicLevelRepository repository,
            Clock clock) {
        return new DeactivateAcademicLevelService(repository, clock);
    }

    // ---- AcademicPeriod Use Cases ----

    @Bean
    public CreateAcademicPeriodService createAcademicPeriodService(
            AcademicPeriodRepository repository,
            Clock clock) {
        return new CreateAcademicPeriodService(repository, clock);
    }

    @Bean
    public GetAcademicPeriodService getAcademicPeriodService(
            AcademicPeriodRepository repository) {
        return new GetAcademicPeriodService(repository);
    }

    @Bean
    public ListAcademicPeriodsByLevelService listAcademicPeriodsByLevelService(
            AcademicPeriodRepository repository) {
        return new ListAcademicPeriodsByLevelService(repository);
    }

    @Bean
    public UpdateAcademicPeriodService updateAcademicPeriodService(
            AcademicPeriodRepository repository,
            Clock clock) {
        return new UpdateAcademicPeriodService(repository, clock);
    }

    @Bean
    public DeactivateAcademicPeriodService deactivateAcademicPeriodService(
            AcademicPeriodRepository repository,
            Clock clock) {
        return new DeactivateAcademicPeriodService(repository, clock);
    }

    // ---- EvaluationPeriod Use Cases ----

    @Bean
    public CreateEvaluationPeriodService createEvaluationPeriodService(
            EvaluationPeriodRepository evaluationRepository,
            AcademicPeriodRepository periodRepository,
            Clock clock) {
        return new CreateEvaluationPeriodService(evaluationRepository, periodRepository, clock);
    }

    @Bean
    public GetEvaluationPeriodService getEvaluationPeriodService(
            EvaluationPeriodRepository repository) {
        return new GetEvaluationPeriodService(repository);
    }

    @Bean
    public ListEvaluationPeriodsByPeriodService listEvaluationPeriodsByPeriodService(
            EvaluationPeriodRepository repository) {
        return new ListEvaluationPeriodsByPeriodService(repository);
    }

    @Bean
    public UpdateEvaluationPeriodService updateEvaluationPeriodService(
            EvaluationPeriodRepository repository,
            Clock clock) {
        return new UpdateEvaluationPeriodService(repository, clock);
    }

    @Bean
    public DeactivateEvaluationPeriodService deactivateEvaluationPeriodService(
            EvaluationPeriodRepository repository,
            Clock clock) {
        return new DeactivateEvaluationPeriodService(repository, clock);
    }

    // ---- Subject Use Cases ----

    @Bean
    public CreateSubjectService createSubjectService(
            SubjectRepository subjectRepository,
            SchoolRepository schoolRepository,
            Clock clock) {
        return new CreateSubjectService(subjectRepository, schoolRepository, clock);
    }

    @Bean
    public GetSubjectService getSubjectService(
            SubjectRepository repository) {
        return new GetSubjectService(repository);
    }

    @Bean
    public ListSubjectsBySchoolService listSubjectsBySchoolService(
            SubjectRepository repository) {
        return new ListSubjectsBySchoolService(repository);
    }

    @Bean
    public UpdateSubjectService updateSubjectService(
            SubjectRepository repository,
            Clock clock) {
        return new UpdateSubjectService(repository, clock);
    }

    @Bean
    public DeactivateSubjectService deactivateSubjectService(
            SubjectRepository repository,
            Clock clock) {
        return new DeactivateSubjectService(repository, clock);
    }

    // ---- Group Use Cases ----

    @Bean
    public CreateGroupService createGroupService(
            GroupRepository groupRepository,
            SchoolRepository schoolRepository,
            SubjectRepository subjectRepository,
            com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository periodRepository,
            BranchRepository branchRepository,
            com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository membershipRepository,
            Clock clock) {
        return new CreateGroupService(groupRepository, schoolRepository, subjectRepository,
                periodRepository, branchRepository, membershipRepository, clock);
    }

    @Bean
    public GetGroupService getGroupService(GroupRepository groupRepository) {
        return new GetGroupService(groupRepository);
    }

    @Bean
    public ListGroupsBySchoolService listGroupsBySchoolService(GroupRepository groupRepository) {
        return new ListGroupsBySchoolService(groupRepository);
    }

    @Bean
    public UpdateGroupService updateGroupService(
            GroupRepository groupRepository,
            SchoolRepository schoolRepository,
            SubjectRepository subjectRepository,
            com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository periodRepository,
            BranchRepository branchRepository,
            MembershipRepository membershipRepository,
            Clock clock) {
        return new UpdateGroupService(groupRepository, schoolRepository, subjectRepository,
                periodRepository, branchRepository, membershipRepository, clock);
    }

    @Bean
    public UpdateGroupSchedulesService updateGroupSchedulesService(
            GroupRepository groupRepository,
            Clock clock) {
        return new UpdateGroupSchedulesService(groupRepository, clock);
    }

    @Bean
    public DeactivateGroupService deactivateGroupService(
            GroupRepository groupRepository,
            Clock clock) {
        return new DeactivateGroupService(groupRepository, clock);
    }

    // ---- Enrollment Use Cases ----

    @Bean
    public com.logossystemsit.logiceducore.application.academic.enrollment.usecase.EnrollStudentService enrollStudentService(
            com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository enrollmentRepository,
            GroupRepository groupRepository,
            com.logossystemsit.logiceducore.application.user.port.out.UserRepository userRepository,
            Clock clock) {
        return new com.logossystemsit.logiceducore.application.academic.enrollment.usecase.EnrollStudentService(
                enrollmentRepository, groupRepository, userRepository, clock);
    }

    @Bean
    public com.logossystemsit.logiceducore.application.academic.enrollment.usecase.GetEnrollmentService getEnrollmentService(
            com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository enrollmentRepository) {
        return new com.logossystemsit.logiceducore.application.academic.enrollment.usecase.GetEnrollmentService(enrollmentRepository);
    }

    @Bean
    public com.logossystemsit.logiceducore.application.academic.enrollment.usecase.ListEnrollmentsByGroupService listEnrollmentsByGroupService(
            com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository enrollmentRepository) {
        return new com.logossystemsit.logiceducore.application.academic.enrollment.usecase.ListEnrollmentsByGroupService(enrollmentRepository);
    }

    @Bean
    public com.logossystemsit.logiceducore.application.academic.enrollment.usecase.DropEnrollmentService dropEnrollmentService(
            com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository enrollmentRepository,
            Clock clock) {
        return new com.logossystemsit.logiceducore.application.academic.enrollment.usecase.DropEnrollmentService(enrollmentRepository, clock);
    }
}
