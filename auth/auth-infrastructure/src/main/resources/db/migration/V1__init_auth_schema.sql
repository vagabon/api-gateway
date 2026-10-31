CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    avatar VARCHAR(255),
    google_id VARCHAR(250),
    last_connexion_date TIMESTAMPTZ,
    active BOOLEAN,
    creation_date TIMESTAMPTZ,
    deleted_date TIMESTAMPTZ,
    updated_date TIMESTAMPTZ
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50),
    role_name VARCHAR(50),
    origin VARCHAR(50),
    end_plan TIMESTAMP,
    active BOOLEAN,
    creation_date TIMESTAMPTZ,
    deleted_date TIMESTAMPTZ,
    updated_date TIMESTAMPTZ
);

CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO public.roles (active, creation_date, name, role_name, origin) VALUES
    (TRUE, '2023-10-30 16:30:34.783169+00', 'USER_BLOG', 'USER', 'http://localhost:4200'),
    (TRUE, '2023-10-30 16:30:34.783169+00', 'ADMIN_BLOG', 'ADMIN', 'http://localhost:4200');