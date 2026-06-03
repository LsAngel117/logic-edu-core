package com.logossystemsit.logiceducore.application.academic.assessment.port.in;

import com.logossystemsit.logiceducore.domain.academic.assessment.model.valueobject.AssessmentId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public interface DeleteAssessmentUseCase {
    void execute(AssessmentId id, GroupId groupId, UserId teacherId);
}
