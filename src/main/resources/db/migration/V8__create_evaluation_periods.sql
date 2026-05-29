CREATE TABLE evaluation_periods (
    id          VARCHAR(36)    PRIMARY KEY,
    period_id   VARCHAR(36)    NOT NULL REFERENCES academic_periods(id),
    name        VARCHAR        NOT NULL,
    sequence    INT,
    weight      DECIMAL(5,2),
    start_date  DATE,
    end_date    DATE,
    status      VARCHAR(20)    NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL,
    updated_at  TIMESTAMPTZ    NOT NULL
);

CREATE INDEX idx_evaluation_periods_period_id ON evaluation_periods(period_id);
