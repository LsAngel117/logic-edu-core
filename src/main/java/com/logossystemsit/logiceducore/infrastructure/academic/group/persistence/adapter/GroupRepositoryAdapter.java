package com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.adapter;

import com.logossystemsit.logiceducore.application.academic.group.port.out.GroupRepository;
import com.logossystemsit.logiceducore.domain.academic.group.model.*;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupId;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.GroupStatus;
import com.logossystemsit.logiceducore.domain.academic.group.model.Schedule;
import com.logossystemsit.logiceducore.domain.academic.group.model.valueobject.ScheduleId;
import com.logossystemsit.logiceducore.domain.academic.period.model.valueobject.AcademicPeriodId;
import com.logossystemsit.logiceducore.domain.academic.subject.model.valueobject.SubjectId;
import com.logossystemsit.logiceducore.domain.branch.model.valueobject.BranchId;
import com.logossystemsit.logiceducore.domain.school.model.valueobject.SchoolId;
import com.logossystemsit.logiceducore.domain.user.model.valueobject.UserId;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity.GroupEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.entity.GroupScheduleEntity;
import com.logossystemsit.logiceducore.infrastructure.academic.group.persistence.repository.GroupJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupRepositoryAdapter implements GroupRepository {

    private final GroupJpaRepository jpa;

    public GroupRepositoryAdapter(GroupJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Group group) {
        GroupEntity entity = mapToEntity(group);
        jpa.save(entity);
    }

    @Override
    public Optional<Group> findById(GroupId id) {
        return jpa.findById(id.value())
                .map(this::mapToDomain);
    }

    @Override
    public List<Group> findBySchoolId(SchoolId schoolId) {
        return jpa.findBySchoolId(schoolId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Group> findBySchoolIdAndBranchId(SchoolId schoolId, BranchId branchId) {
        return jpa.findBySchoolIdAndBranchId(schoolId.value(), branchId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Group> findBySchoolIdAndPeriodId(SchoolId schoolId, AcademicPeriodId periodId) {
        return jpa.findBySchoolIdAndAcademicPeriodId(schoolId.value(), periodId.value()).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public boolean existsBySchoolIdAndCode(SchoolId schoolId, String code) {
        return jpa.existsBySchoolIdAndCode(schoolId.value(), code);
    }

    private GroupEntity mapToEntity(Group group) {
        GroupEntity entity = new GroupEntity();
        entity.setId(group.getId().value());
        entity.setSchoolId(group.getSchoolId().value());
        entity.setSubjectId(group.getSubjectId().value());
        entity.setAcademicPeriodId(group.getAcademicPeriodId().value());
        entity.setBranchId(group.getBranchId().value());
        entity.setTeacherId(group.getTeacherId().value());
        entity.setCode(group.getCode());
        entity.setCapacity(group.getCapacity());
        entity.setStatus(group.getStatus().name());
        entity.setVersion(group.getVersion());
        entity.setCreatedAt(group.getCreatedAt());
        entity.setUpdatedAt(group.getUpdatedAt());

        List<GroupScheduleEntity> scheduleEntities = group.getSchedules().stream()
                .map(s -> mapScheduleToEntity(s, entity))
                .collect(Collectors.toList());
        entity.setSchedules(scheduleEntities);

        return entity;
    }

    private GroupScheduleEntity mapScheduleToEntity(Schedule schedule, GroupEntity groupEntity) {
        GroupScheduleEntity entity = new GroupScheduleEntity();
        entity.setId(schedule.scheduleId().value());
        entity.setGroup(groupEntity);
        entity.setDayOfWeek(schedule.dayOfWeek());
        entity.setStartTime(schedule.startTime());
        entity.setEndTime(schedule.endTime());
        entity.setClassroom(schedule.classroom());
        return entity;
    }

    private Group mapToDomain(GroupEntity entity) {
        List<Schedule> schedules = entity.getSchedules() != null
                ? entity.getSchedules().stream()
                    .map(this::mapScheduleToDomain)
                    .toList()
                : List.of();

        return Group.restore(
                new GroupId(entity.getId()),
                new SchoolId(entity.getSchoolId()),
                new SubjectId(entity.getSubjectId()),
                new AcademicPeriodId(entity.getAcademicPeriodId()),
                BranchId.of(entity.getBranchId()),
                new UserId(entity.getTeacherId()),
                entity.getCode(),
                entity.getCapacity(),
                schedules,
                GroupStatus.valueOf(entity.getStatus()),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private Schedule mapScheduleToDomain(GroupScheduleEntity entity) {
        return new Schedule(
                new ScheduleId(entity.getId()),
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getClassroom()
        );
    }
}
