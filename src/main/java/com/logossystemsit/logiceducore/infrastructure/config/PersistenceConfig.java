package com.logossystemsit.logiceducore.infrastructure.config;

import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.application.academic.enrollment.port.out.EnrollmentRepository;
import com.logossystemsit.logiceducore.application.academic.evaluation.port.out.EvaluationPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.level.port.out.AcademicLevelRepository;
import com.logossystemsit.logiceducore.application.academic.period.port.out.AcademicPeriodRepository;
import com.logossystemsit.logiceducore.application.academic.structure.port.out.AcademicStructureRepository;
import com.logossystemsit.logiceducore.application.academic.subject.port.out.SubjectRepository;
import com.logossystemsit.logiceducore.application.branch.port.out.BranchRepository;
import com.logossystemsit.logiceducore.application.membership.port.out.MembershipRepository;
import com.logossystemsit.logiceducore.application.school.port.out.SchoolRepository;
import com.logossystemsit.logiceducore.application.user.port.out.UserRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.adapter.GroupRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.repository.GroupJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.adapter.EnrollmentRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.enrollment.persistence.repository.EnrollmentJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.adapter.EvaluationPeriodRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.evaluation.persistence.repository.EvaluationPeriodJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.adapter.AcademicLevelRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.level.persistence.repository.AcademicLevelJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.adapter.AcademicPeriodRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.period.persistence.repository.AcademicPeriodJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.adapter.AcademicStructureRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.structure.persistence.repository.AcademicStructureJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.adapter.SubjectRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.academic.subject.persistence.repository.SubjectJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.adapter.BranchRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.branch.persistence.repository.BranchJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.adapter.MembershipRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.membership.persistence.repository.MembershipJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.adapter.SchoolRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.school.persistence.repository.SchoolJpaRepository;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.adapter.UserRepositoryAdapter;
import com.logossystemsit.logiceducore.infrastructure.user.persistence.repository.UserJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {

    @Bean
    public UserRepository userRepositoryAdapter(UserJpaRepository jpa) {
        return new UserRepositoryAdapter(jpa);
    }

    @Bean
    public MembershipRepository membershipRepositoryAdapter(MembershipJpaRepository jpa) {
        return new MembershipRepositoryAdapter(jpa);
    }

    @Bean
    public SchoolRepository schoolRepositoryAdapter(SchoolJpaRepository jpa) {
        return new SchoolRepositoryAdapter(jpa);
    }

    @Bean
    public BranchRepository branchRepositoryAdapter(BranchJpaRepository jpa) {
        return new BranchRepositoryAdapter(jpa);
    }

    @Bean
    public AcademicStructureRepository academicStructureRepositoryAdapter(AcademicStructureJpaRepository jpa) {
        return new AcademicStructureRepositoryAdapter(jpa);
    }

    @Bean
    public AcademicLevelRepository academicLevelRepositoryAdapter(AcademicLevelJpaRepository jpa) {
        return new AcademicLevelRepositoryAdapter(jpa);
    }

    @Bean
    public AcademicPeriodRepository academicPeriodRepositoryAdapter(AcademicPeriodJpaRepository jpa) {
        return new AcademicPeriodRepositoryAdapter(jpa);
    }

    @Bean
    public EvaluationPeriodRepository evaluationPeriodRepositoryAdapter(EvaluationPeriodJpaRepository jpa) {
        return new EvaluationPeriodRepositoryAdapter(jpa);
    }

    @Bean
    public SubjectRepository subjectRepositoryAdapter(SubjectJpaRepository jpa) {
        return new SubjectRepositoryAdapter(jpa);
    }

    @Bean
    public GroupRepository groupRepositoryAdapter(GroupJpaRepository jpa) {
        return new GroupRepositoryAdapter(jpa);
    }

    @Bean
    public EnrollmentRepository enrollmentRepositoryAdapter(EnrollmentJpaRepository jpa) {
        return new EnrollmentRepositoryAdapter(jpa);
    }
}
