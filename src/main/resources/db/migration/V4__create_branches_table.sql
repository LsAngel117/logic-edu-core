CREATE TABLE branches (
    id          VARCHAR(36)  PRIMARY KEY,
    school_id   VARCHAR(36)  NOT NULL REFERENCES schools(id),
    name        VARCHAR      NOT NULL,
    code        VARCHAR      NOT NULL,
    short_name  VARCHAR      NOT NULL,
    description VARCHAR,
    email       VARCHAR,
    phone       VARCHAR,
    address     VARCHAR,
    type        VARCHAR(10)  NOT NULL,
    status      VARCHAR(10)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX idx_branches_school_name ON branches(school_id, name);
CREATE INDEX idx_branches_school_id ON branches(school_id);
CREATE UNIQUE INDEX idx_branches_main ON branches(school_id) WHERE type = 'MAIN';
