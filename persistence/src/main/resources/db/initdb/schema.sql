
CREATE TABLE IF NOT EXISTS users (
    userid SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS game (
        id BIGSERIAL PRIMARY KEY,
        name text NOT NULL UNIQUE,
        genre VARCHAR(100)
);


CREATE TABLE IF NOT EXISTS tournament (
        id SERIAL PRIMARY KEY,
        creatorid INT NOT NULL,
        name text NOT NULL,
        gameid INT NOT NULL,
        region region_enum,
        elo elo_enum,
        startdate date,
        enddate date,
        format TEXT NOT NULL,
        structure structure_enum,
        max_participants INT NOT NULL,
        CONSTRAINT tournament_dates_check CHECK (startdate IS NULL OR enddate IS NULL OR startdate < enddate)
);

