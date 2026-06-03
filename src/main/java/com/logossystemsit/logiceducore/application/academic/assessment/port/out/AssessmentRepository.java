package com.logossystemsit.logiceducore.application.academic.assessment.port.out;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.Assessment;
import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository {

    void save(Assessment assessment);

    Optional<Assessment> findById(AssessmentId id);

    List<Assessment> findByGroupId(GroupId groupId);

    boolean existsByGroupIdAndName(GroupId groupId, String name);

    void delete(Assessment assessment);
}
