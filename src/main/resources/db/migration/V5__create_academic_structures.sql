CREATE TABLE academic_structures (
    id                                VARCHAR(36)  PRIMARY KEY,
    school_id                         VARCHAR(36)  NOT NULL,
    structure_type                    VARCHAR(20)  NOT NULL,
    levels_count                      INT          NOT NULL,
    periods_per_level                 INT          NOT NULL,
    evaluation_periods_per_period     INT          DEFAULT 0,
    subjects_per_period               INT          NOT NULL,
    hours_per_subject                 INT          NOT NULL,
    active                            BOOLEAN      DEFAULT TRUE,
    version                           INT          DEFAULT 1,
    created_at                        TIMESTAMPTZ  NOT NULL,
    updated_at                        TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX idx_academic_structures_school_version ON academic_structures(school_id, version);
CREATE INDEX idx_academic_structures_school_active ON academic_structures(school_id, active);
