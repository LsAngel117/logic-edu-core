package com.logossystemsit.logiceducore.application.academic.subject.port.in;

import com.logossystemsit.logiceducore.application.academic.subject.dto.command.UpdateSubjectCommand;
import com.logossystemsit.logiceducore.application.academic.subject.dto.result.SubjectResult;

public interface UpdateSubjectUseCase {

    SubjectResult execute(UpdateSubjectCommand command);
}
