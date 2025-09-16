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