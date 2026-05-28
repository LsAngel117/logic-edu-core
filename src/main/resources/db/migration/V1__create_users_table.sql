CREATE TABLE users (
    id              VARCHAR(36) PRIMARY KEY,
    username        VARCHAR(21) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_given_name  VARCHAR(50) NOT NULL,
    second_given_name VARCHAR(50),
    first_family_name VARCHAR(50) NOT NULL,
    second_family_name VARCHAR(50),
    sex             VARCHAR(10) NOT NULL,
    birth_date      DATE NOT NULL,
    document_type   VARCHAR(10) NOT NULL,
    document_value  VARCHAR(20) NOT NULL,
    status          VARCHAR(10) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
