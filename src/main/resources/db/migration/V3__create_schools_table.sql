CREATE TABLE schools (
    id          VARCHAR(36) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    code        VARCHAR(20) NOT NULL UNIQUE,
    short_name  VARCHAR(80) NOT NULL,
    description VARCHAR(500),
    email       VARCHAR(254),
    phone       VARCHAR(16),
    address     VARCHAR(255),
    status      VARCHAR(10) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX idx_schools_name ON schools(name);
CREATE UNIQUE INDEX idx_schools_code ON schools(code);
