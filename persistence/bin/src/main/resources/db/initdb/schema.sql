CREATE TABLE IF NOT EXISTS users (
    userid SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS game (
        id BIGSERIAL PRIMARY KEY,
        name text NOT NULL UNIQUE,
        genre genre_enum
);

