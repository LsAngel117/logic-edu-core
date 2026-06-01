CREATE TABLE enrollments (
    id          VARCHAR(36)  PRIMARY KEY,
    user_id     VARCHAR(36)  NOT NULL REFERENCES users(id),
    group_id    VARCHAR(36)  NOT NULL REFERENCES groups(id),
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    enrolled_at TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX idx_enrollments_user_group ON enrollments(user_id, group_id);
CREATE INDEX idx_enrollments_group_id ON enrollments(group_id);
CREATE INDEX idx_enrollments_user_id ON enrollments(user_id);
