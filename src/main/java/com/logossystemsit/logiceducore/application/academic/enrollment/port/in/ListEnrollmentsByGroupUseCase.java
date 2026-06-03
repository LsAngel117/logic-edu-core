package com.logossystemsit.logiceducore.application.academic.enrollment.port.in;

import com.logossystemsit.logiceducore.application.academic.enrollment.dto.result.EnrollmentResult;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;

import java.util.List;

public interface ListEnrollmentsByGroupUseCase {

    List<EnrollmentResult> execute(GroupId groupId);
}
