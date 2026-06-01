CREATE TABLE groups (
    id                  VARCHAR(36)  PRIMARY KEY,
    school_id           VARCHAR(36)  NOT NULL REFERENCES schools(id),
    subject_id          VARCHAR(36)  NOT NULL REFERENCES subjects(id),
    academic_period_id  VARCHAR(36)  NOT NULL REFERENCES academic_periods(id),
    branch_id           VARCHAR(36)  NOT NULL REFERENCES branches(id),
    teacher_id          VARCHAR(36)  NOT NULL,
    code                VARCHAR      NOT NULL,
    capacity            INT          NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    version             BIGINT       DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX idx_groups_school_code ON groups(school_id, code);

CREATE TABLE group_schedules (
    id          VARCHAR(36)  PRIMARY KEY,
    group_id    VARCHAR(36)  NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    day_of_week VARCHAR(10)  NOT NULL,
    start_time  TIME         NOT NULL,
    end_time    TIME         NOT NULL,
    classroom   VARCHAR
);

CREATE UNIQUE INDEX idx_schedules_group_day_start ON group_schedules(group_id, day_of_week, start_time);
