package com.logossystemsit.logiceducore.application.academic.subject.dto.command;

import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;

public record CreateSubjectCommand(
        SubjectId subjectId,
        SchoolId schoolId,
        String code,
        String name,
        String description,
        int hours
) {}
