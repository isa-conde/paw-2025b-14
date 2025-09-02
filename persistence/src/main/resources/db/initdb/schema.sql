
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

CREATE TABLE IF NOT EXISTS participant_user (
        user_id INT NOT NULL,
        tournament_id INT NOT NULL,
        points INT NOT NULL DEFAULT 0,
        PRIMARY KEY(user_id, tournament_id),
        FOREIGN KEY (user_id) REFERENCES users(userid) ON DELETE CASCADE,
        FOREIGN KEY (tournament_id) REFERENCES tournament(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS match (
	id INT,
	tournament_id INT NOT NULL,
	local_id INT,
	visitor_id INT,
	local_score INT,
	visitor_score INT,
	PRIMARY KEY (id, tournament_id),
	FOREIGN KEY (tournament_id) REFERENCES tournament(id) ON DELETE CASCADE,
	FOREIGN KEY (local_id) REFERENCES users(userid),
	FOREIGN KEY (visitor_id) REFERENCES users(userid)
);