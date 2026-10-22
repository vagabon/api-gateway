CREATE TABLE "user" (
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

CREATE TABLE profile (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    roles VARCHAR(255),
    end_plan TIMESTAMP,
    active BOOLEAN,
    creation_date TIMESTAMPTZ,
    deleted_date TIMESTAMPTZ,
    updated_date TIMESTAMPTZ
);

CREATE TABLE user_profile (
    user_id BIGINT REFERENCES "user"(id) ON DELETE CASCADE,
    profile_id BIGINT REFERENCES profile(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, profile_id)
);

INSERT INTO public.profile (active, creation_date, name, roles) VALUES
    (TRUE, '2023-04-01 16:30:34.783169+00', 'USER', 'USER'),
    (TRUE, '2023-04-01 16:30:34.783169+00', 'MEMBER', 'USER,MEMBER'),
    (TRUE, '2023-04-01 16:30:34.783169+00', 'ADMIN', 'USER,MEMBER,ADMIN');