CREATE TABLE assessments (
    id                  VARCHAR(36)   PRIMARY KEY,
    group_id            VARCHAR(36)   NOT NULL REFERENCES groups(id),
    evaluation_period_id VARCHAR(36)  REFERENCES evaluation_periods(id),
    name                VARCHAR       NOT NULL,
    type                VARCHAR       NOT NULL,
    weight              DECIMAL(5,2)  NOT NULL,
    max_score           DECIMAL(5,2)  NOT NULL,
    created_at          TIMESTAMPTZ   NOT NULL,
    updated_at          TIMESTAMPTZ   NOT NULL
);

CREATE UNIQUE INDEX idx_assessments_group_name ON assessments(group_id, name);
