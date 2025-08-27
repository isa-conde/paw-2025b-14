CREATE TABLE IF NOT EXISTS users (
    userid SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE game (
        id BIGSERIAL PRIMARY KEY,
        name text NOT NULL UNIQUE,
        genre genre_enum
);

CREATE TYPE genre_enum AS ENUM ('MOBA', 'FPS', 'Fighting', 'TPS', 'BattleRoyale', 'RTS', 'Sports', 'DGC');
