package com.logossystemsit.logiceducore.application.school.port.in;

import com.logossystemsit.logiceducore.application.school.dto.command.UpdateSchoolCommand;
import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;

public interface UpdateSchoolUseCase {

    SchoolResult execute(UpdateSchoolCommand command);
}
