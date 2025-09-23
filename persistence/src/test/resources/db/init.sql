CREATE TABLE IF NOT EXISTS users(
    id INTEGER IDENTITY PRIMARY KEY ,
    email varchar(100) NOT NULL UNIQUE ,
    username varchar(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS game(
    id INTEGER IDENTITY PRIMARY KEY ,
    name varchar(100) NOT NULL UNIQUE ,
    genre varchar(20) ,
    image_id INTEGER
);

CREATE TABLE IF NOT EXISTS tournament(
    id INTEGER IDENTITY PRIMARY KEY ,
    creator_id INTEGER NOT NULL ,
    name varchar(50) NOT NULL ,
    game_id INTEGER NOT NULL ,
    region varchar(10) ,
    elo varchar(10) ,
    start_date date ,
    end_date date ,
    format varchar(100) NOT NULL ,
    max_participants INTEGER NOT NULL ,
    structure varchar(20) ,
    image_id INTEGER ,
    open_inscriptions BOOLEAN ,
    is_finished BOOLEAN ,
    CONSTRAINT tournament_dates_check
        CHECK  ((start_date IS NULL) OR (end_date IS NULL) OR (start_date < end_date))
);

CREATE TABLE IF NOT EXISTS image(
    id INTEGER IDENTITY PRIMARY KEY ,
    image varbinary(1000000)
)