package com.logossystemsit.logiceducore.application.academic.subject.port.in;

import com.logossystemsit.logiceducore.application.academic.subject.dto.command.CreateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;

public interface CreateSubjectUseCase {

    SubjectResult execute(CreateSubjectCommand command);
}
