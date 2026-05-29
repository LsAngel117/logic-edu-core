package com.logossystemsit.logiceducore.application.school.port.in;

import com.logossystemsit.logiceducore.application.school.dto.command.CreateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;

public interface CreateSchoolUseCase {

    SchoolResult execute(CreateSchoolCommand command);
}
