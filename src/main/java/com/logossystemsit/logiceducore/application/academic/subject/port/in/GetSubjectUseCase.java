package com.logossystemsit.logiceducore.application.academic.subject.port.in;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;

public interface GetSubjectUseCase {

    SubjectResult execute(SubjectId id);
}
