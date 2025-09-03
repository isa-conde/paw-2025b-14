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
        creator_id INT NOT NULL,
        name text NOT NULL,
        game_id INT NOT NULL,
        region region_enum,
        elo elo_enum,
        start_date date,
        end_date date,
        format TEXT NOT NULL,
        structure structure_enum,
        max_participants INT NOT NULL,
        CONSTRAINT tournament_dates_check CHECK (start_date IS NULL OR end_date IS NULL OR start_date < end_date)
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

CREATE TABLE IF NOT EXISTS game_format (
	id SERIAL PRIMARY KEY NOT NULL,
	name TEXT NOT NULL,
	players_per_team smallint NOT NULL,
	game_id INT NOT NULL,
	FOREIGN KEY (game_id) REFERENCES game(id)
);

CREATE TABLE IF NOT EXISTS image(
	id SERIAL PRIMARY KEY NOT NULL,
	image BYTEA
);

ALTER TABLE game ADD COLUMN IF NOT EXISTS image_id INT;
UPDATE game SET image_id = 2 WHERE image_id IS NULL;
ALTER TABLE tournament ADD COLUMN IF NOT EXISTS image_id INT;
UPDATE tournament SET image_id = 2 WHERE image_id IS NULL;
ALTER TABLE tournament ADD COLUMN IF NOT EXISTS open_inscriptions BOOLEAN;
ALTER TABLE tournament ADD COLUMN IF NOT EXISTS is_finished BOOLEAN;
UPDATE tournament SET open_inscriptions = TRUE WHERE open_inscriptions IS NULL;
UPDATE tournament SET is_finished = FALSE WHERE is_finished IS NULL;