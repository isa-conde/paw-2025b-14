CREATE TABLE rules(
                      id SERIAL PRIMARY KEY,
                      file bytea NOT NULL);

ALTER TABLE tournament ADD COLUMN rules_id bigint;

ALTER TABLE tournament ADD CONSTRAINT fk_rules_id FOREIGN KEY (rules_id) REFERENCES rules(id);

ALTER TABLE participant
    ADD COLUMN score_difference INTEGER NOT NULL DEFAULT 0;