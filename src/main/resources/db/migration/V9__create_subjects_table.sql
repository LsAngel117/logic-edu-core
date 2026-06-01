CREATE TABLE subjects (
    id          VARCHAR(36)  PRIMARY KEY,
    school_id   VARCHAR(36)  NOT NULL REFERENCES schools(id),
    code        VARCHAR      NOT NULL,
    name        VARCHAR      NOT NULL,
    description VARCHAR,
    hours       INT          NOT NULL DEFAULT 0,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX idx_subjects_school_code ON subjects(school_id, code);
