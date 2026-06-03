package com.logossystemsit.logiceducore.application.academic.grade.port.out;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.grade.model.Grade;
import com.logossystemsit.logiceducore.domain.academic.grade.model.valueobject.GradeId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

import java.util.List;
import java.util.Optional;

public interface GradeRepository {

    void save(Grade grade);

    Optional<Grade> findById(GradeId id);

    List<Grade> findByAssessmentId(AssessmentId assessmentId);

    Optional<Grade> findByAssessmentIdAndStudentId(AssessmentId assessmentId, UserId studentId);

    long countByAssessmentId(AssessmentId assessmentId);
}
