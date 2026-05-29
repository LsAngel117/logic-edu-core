CREATE TABLE academic_levels (
    id          VARCHAR(36)  PRIMARY KEY,
    school_id   VARCHAR(36)  NOT NULL,
    name        VARCHAR      NOT NULL,
    number      INT          NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX idx_academic_levels_school_number ON academic_levels(school_id, number);
