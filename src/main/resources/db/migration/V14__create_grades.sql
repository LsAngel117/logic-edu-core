CREATE TABLE grades (
    id            VARCHAR(36)   PRIMARY KEY,
    assessment_id VARCHAR(36)   NOT NULL REFERENCES assessments(id),
    student_id    VARCHAR(36)   NOT NULL,
    value         DECIMAL(5,2)  NOT NULL,
    graded_at     TIMESTAMPTZ   NOT NULL,
    updated_at    TIMESTAMPTZ   NOT NULL
);

CREATE UNIQUE INDEX idx_grades_assessment_student ON grades(assessment_id, student_id);
CREATE INDEX idx_grades_assessment_id ON grades(assessment_id);
