CREATE TABLE attendances (
    id          VARCHAR(36)  PRIMARY KEY,
    group_id    VARCHAR(36)  NOT NULL REFERENCES groups(id),
    student_id  VARCHAR(36)  NOT NULL,
    date        DATE         NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    observations VARCHAR,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX idx_attendances_group_date_student ON attendances(group_id, date, student_id);
CREATE INDEX idx_attendances_group_id ON attendances(group_id);
