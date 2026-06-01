package com.logossystemsit.logiceducore.application.academic.group.dto.command;

import com.logossystemsit.logiceducore.domain.academic.period.model.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.SubjectId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;

public record UpdateGroupCommand(
        com.logossystemsit.logiceducore.domain.academic.group.model.GroupId groupId,
        SchoolId schoolId,
        SubjectId subjectId,
        AcademicPeriodId academicPeriodId,
        BranchId branchId,
        UserId teacherId,
        String code,
        int capacity
) {}
