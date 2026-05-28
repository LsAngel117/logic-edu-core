CREATE TABLE memberships (
    id            VARCHAR(36) PRIMARY KEY,
    user_id       VARCHAR(36) NOT NULL REFERENCES users(id),
    role          VARCHAR(30) NOT NULL,
    scope_type    VARCHAR(20) NOT NULL,
    scope_ref_id  VARCHAR(36),
    active        BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_memberships_user_id ON memberships(user_id);
