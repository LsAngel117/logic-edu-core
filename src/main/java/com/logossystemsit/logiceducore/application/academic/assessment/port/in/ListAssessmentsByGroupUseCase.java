package com.logossystemsit.logiceducore.application.academic.assessment.port.in;

import com.logossystemsit.logiceducore.application.academic.assessment.dto.result.AssessmentResult;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;

import java.util.List;

public interface ListAssessmentsByGroupUseCase {
    List<AssessmentResult> execute(GroupId groupId);
}
