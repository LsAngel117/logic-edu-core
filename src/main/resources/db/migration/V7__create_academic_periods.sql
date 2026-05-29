CREATE TABLE academic_periods (
    id          VARCHAR(36)  PRIMARY KEY,
    level_id    VARCHAR(36)  NOT NULL REFERENCES academic_levels(id),
    period_type VARCHAR(20)  NOT NULL,
    name        VARCHAR      NOT NULL,
    sequence    INT,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_academic_periods_level_dates ON academic_periods(level_id, start_date, end_date);
