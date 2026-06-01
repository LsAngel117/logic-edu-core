package com.logossystemsit.logiceducore.application.academic.enrollment.port.out;

import com.logossystemsit.logiceducore.domain.academic.enrollment.model.Enrollment;
import com.logossystemsit.logiceducore.domain.academic.enrollment.model.EnrollmentId;
import com.logossystemsit.logiceducore.domain.academic.group.model.GroupId;
import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {

    void save(Enrollment enrollment);

    Optional<Enrollment> findById(EnrollmentId id);

    List<Enrollment> findByGroupId(GroupId groupId);

    long countActiveByGroupId(GroupId groupId);

    boolean existsByUserIdAndGroupId(UserId userId, GroupId groupId);

    boolean existsActiveByStudentAndSubjectAndPeriod(UserId userId, SubjectId subjectId, AcademicPeriodId periodId);
}
