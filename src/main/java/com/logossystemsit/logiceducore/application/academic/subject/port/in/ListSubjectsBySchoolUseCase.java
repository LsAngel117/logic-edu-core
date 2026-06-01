package com.logossystemsit.logiceducore.application.academic.subject.port.in;

import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

import java.util.List;

public interface ListSubjectsBySchoolUseCase {

    List<SubjectResult> execute(SchoolId schoolId);
}
