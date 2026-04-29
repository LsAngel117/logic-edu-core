package com.logossystemsit.logiceducore.domain.school.model;

import com.logossystemsit.logiceducore.domain.school.model.valueobject.*;

import java.time.Instant;
import java.util.Objects;

public class School {

    private final SchoolId id;
    private final SchoolName name;
    private final SchoolCode code;
    private final Status status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private School(SchoolId id,
                   SchoolName name,
                   SchoolCode code,
                   Status status,
                   Instant createdAt,
                   Instant updatedAt) {

        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.code = Objects.requireNonNull(code);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static School create(SchoolId id, SchoolName name, SchoolCode code, Instant now) {
        return new School(id, name, code, Status.ACTIVE, now, now);
    }

    public School deactivate(Instant now) {
        return new School(id, name, code, Status.INACTIVE, createdAt, now);
    }

    public enum Status {
        ACTIVE, INACTIVE
    }
}
