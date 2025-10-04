CREATE TABLE IF NOT EXISTS users(
    id INTEGER IDENTITY PRIMARY KEY ,
    email varchar(100) NOT NULL UNIQUE ,
    username varchar(100) NOT NULL UNIQUE ,
    password varchar (100) NULL ,
    verified BOOLEAN DEFAULT false NOT NULL,
    bio varchar(255),
    profile_picture_id integer,
    banner_id integer
);

create table if not exists user_favourites(
    user_id INTEGER NOT NULL ,
    game_id INTEGER NOT NULL,
    primary key (user_id, game_id)
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
    structure varchar(20) ,
    max_participants INTEGER NOT NULL ,
    image_id INTEGER ,
    open_inscriptions BOOLEAN ,
    is_finished BOOLEAN ,
    tournament_winner integer,
    is_group_stage boolean,
    tournament_started boolean,
    CONSTRAINT tournament_dates_check
        CHECK  ((start_date IS NULL) OR (end_date IS NULL) OR (start_date < end_date))
);

CREATE TABLE IF NOT EXISTS image(
    id INTEGER IDENTITY PRIMARY KEY ,
    image varbinary(1000000)
);

create table if not exists game_format(
    id integer identity primary key not null,
    name varchar(100) not null ,
    players_per_team integer not null ,
    game_id integer not null
);

create table if not exists tokens(
    id integer identity primary key not null,
    user_id integer not null,
    token bigint not null,
    expiry_date date not null,
    used BOOLEAN DEFAULT false NOT NULL
);

create table if not exists team_member(
    team_id integer not null,
    user_id integer not null,
    primary key (user_id,team_id)
);

create table if not exists team(
    id integer identity primary key not null ,
    name varchar(16) not null ,
    profile_picture_id integer,
    banner_id integer,
    owner_id integer not null
);

create table if not exists participant_user(
    user_id integer not null ,
    tournament_id integer not null ,
    points integer default 0 not null ,
    group_number integer,
    primary key (user_id,tournament_id)
);

create table if not exists match(
    id integer identity primary key not null,
    tournament_id integer not null ,
    local_id integer,
    visitor_id integer,
    winner integer,
    stage integer,
    group_number integer,
    is_group_stage boolean
);