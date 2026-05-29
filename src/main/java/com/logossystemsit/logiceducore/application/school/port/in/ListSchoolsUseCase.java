package com.logossystemsit.logiceducore.application.school.port.in;

import com.logossystemsit.logiceducore.application.school.dto.result.SchoolResult;

import java.util.List;

public interface ListSchoolsUseCase {

    List<SchoolResult> execute();
}
