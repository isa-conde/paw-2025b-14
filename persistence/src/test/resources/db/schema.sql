set database sql syntax pgs true;
set ignorecase true;

insert into users (id, email, username, password, verified, locale)
    values (100, 'another@mail.com','janedoe','1234567890',false,'es');
insert into users (id, email, username, password, verified, locale)
    values (101, 'another2@mail.com','janetdoe','1234567890',false,'es');
insert into users (id, email, username, password, verified, locale)
    values (102, 'another3@mail.com','johandoe','1234567890',false,'es');
insert into users (id, email, username, password, verified, locale)
    values (103, 'another4@mail.com','joedoe','1234567890',false,'es');

insert into game (id, name, genre, image_id) values (100, 'Grand Theft Walrus MOBA', 'MOBA', 100);
insert into game (id, name, genre, image_id) values (101, 'Grand Theft Walrus FPS', 'FPS', 100);
insert into game (id, name, genre, image_id) values (102, 'Grand Theft Walrus Fighting', 'Fighting', 100);
insert into game (id, name, genre, image_id) values (103, 'Grand Theft Walrus TPS', 'TPS', 100);
insert into game (id, name, genre, image_id) values (104, 'Grand Theft Walrus BattleRoyale', 'BattleRoyale', 100);
insert into game (id, name, genre, image_id) values (105, 'Grand Theft Walrus RTS', 'RTS', 100);
insert into game (id, name, genre, image_id) values (106, 'Grand Theft Walrus Sports', 'Sports', 100);
insert into game (id, name, genre, image_id) values (107, 'Grand Theft Walrus DGC', 'DGC', 100);
insert into game (id, name, genre, image_id) values (108, 'Grand Theft Walrus MOBILE', 'MOBILE', 100);
insert into game (id, name, genre, image_id) values (110, 'F-MEGA MOBA', 'MOBA', 100);
insert into game (id, name, genre, image_id) values (111, 'F-MEGA FPS', 'FPS', 100);
insert into game (id, name, genre, image_id) values (112, 'F-MEGA Fighting', 'Fighting', 100);
insert into game (id, name, genre, image_id) values (113, 'F-MEGA TPS', 'TPS', 100);
insert into game (id, name, genre, image_id) values (114, 'F-MEGA BattleRoyale', 'BattleRoyale', 100);
insert into game (id, name, genre, image_id) values (115, 'F-MEGA RTS', 'RTS', 100);
insert into game (id, name, genre, image_id) values (116, 'F-MEGA Sports', 'Sports', 100);
insert into game (id, name, genre, image_id) values (117, 'F-MEGA DGC', 'DGC', 100);
insert into game (id, name, genre, image_id) values (118, 'F-MEGA MOBILE', 'MOBILE', 100);
insert into game (id, name, genre, image_id) values (120, 'Chimpokomon MOBA', 'MOBA', 100);
insert into game (id, name, genre, image_id) values (121, 'Chimpokomon FPS', 'FPS', 100);
insert into game (id, name, genre, image_id) values (122, 'Chimpokomon Fighting', 'Fighting', 100);
insert into game (id, name, genre, image_id) values (123, 'Chimpokomon TPS', 'TPS', 100);
insert into game (id, name, genre, image_id) values (124, 'Chimpokomon BattleRoyale', 'BattleRoyale', 100);
insert into game (id, name, genre, image_id) values (125, 'Chimpokomon RTS', 'RTS', 100);
insert into game (id, name, genre, image_id) values (126, 'Chimpokomon Sports', 'Sports', 100);
insert into game (id, name, genre, image_id) values (127, 'Chimpokomon DGC', 'DGC', 100);
insert into game (id, name, genre, image_id) values (128, 'Chimpokomon MOBILE', 'MOBILE', 100);
insert into game (id, name, genre, image_id) values (130, 'Lee Carvallo''s Putting Challenge MOBA', 'MOBA', 100);
insert into game (id, name, genre, image_id) values (131, 'Lee Carvallo''s Putting Challenge FPS', 'FPS', 100);
insert into game (id, name, genre, image_id) values (132, 'Lee Carvallo''s Putting Challenge Fighting', 'Fighting', 100);
insert into game (id, name, genre, image_id) values (133, 'Lee Carvallo''s Putting Challenge TPS', 'TPS', 100);
insert into game (id, name, genre, image_id) values (134, 'Lee Carvallo''s Putting Challenge BattleRoyale', 'BattleRoyale', 100);
insert into game (id, name, genre, image_id) values (135, 'Lee Carvallo''s Putting Challenge RTS', 'RTS', 100);
insert into game (id, name, genre, image_id) values (136, 'Lee Carvallo''s Putting Challenge Sports', 'Sports', 100);
insert into game (id, name, genre, image_id) values (137, 'Lee Carvallo''s Putting Challenge DGC', 'DGC', 100);
insert into game (id, name, genre, image_id) values (138, 'Lee Carvallo''s Putting Challenge MOBILE', 'MOBILE', 100);
insert into game (id, name, genre, image_id) values (140, 'Bonestorm MOBA', 'MOBA', 100);
insert into game (id, name, genre, image_id) values (141, 'Bonestorm FPS', 'FPS', 100);
insert into game (id, name, genre, image_id) values (142, 'Bonestorm Fighting', 'Fighting', 100);
insert into game (id, name, genre, image_id) values (143, 'Bonestorm TPS', 'TPS', 100);
insert into game (id, name, genre, image_id) values (144, 'Bonestorm BattleRoyale', 'BattleRoyale', 100);
insert into game (id, name, genre, image_id) values (145, 'Bonestorm RTS', 'RTS', 100);
insert into game (id, name, genre, image_id) values (146, 'Bonestorm Sports', 'Sports', 100);
insert into game (id, name, genre, image_id) values (147, 'Bonestorm DGC', 'DGC', 100);
insert into game (id, name, genre, image_id) values (148, 'Bonestorm MOBILE', 'MOBILE', 100);

insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (100, 100, 'Jerma Rumble open x', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (101, 100, 'Jerma Rumble close x', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (102, 100, 'Jerma Rumble open', 100, 'LAS', 'LOW', '2025-02-21', '2025-04-20', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (103, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (104, 100, 'Jerma Rumble open', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (105, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (106, 100, 'Jerma Rumble open', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (107, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (108, 100, 'Jerma Rumble open', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (109, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (110, 100, 'Jerma Rumble open', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (111, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (112, 100, 'Jerma Rumble open', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (113, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (114, 100, 'Jerma Rumble open', 101, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (115, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (116, 100, 'Jerma Rumble open', 101, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (117, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (118, 100, 'Jerma Rumble open', 102, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (119, 100, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (120, 101, 'Jerma Rumble open', 103, 'LAN', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (121, 101, 'Jerma Rumble close', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,false, true,100,100);

insert into image (id, image) values (100, HEXTORAW(REPEAT('30', 64)));
insert into image (id, image) values (101, HEXTORAW(REPEAT('31', 64)));
insert into image (id, image) values (102, HEXTORAW(REPEAT('32', 64)));
insert into image (id, image) values (103, HEXTORAW(REPEAT('33', 64)));
insert into image (id, image) values (104, HEXTORAW(REPEAT('34', 64)));
insert into image (id, image) values (105, HEXTORAW(REPEAT('35', 64)));
insert into image (id, image) values (106, HEXTORAW(REPEAT('36', 64)));
insert into image (id, image) values (107, HEXTORAW(REPEAT('37', 64)));
insert into image (id, image) values (108, HEXTORAW(REPEAT('38', 64)));

insert into rules (id, file) values (100, HEXTORAW(REPEAT('30', 64)));

insert into tokens (id, user_id, token, expiry_date, used) values (100,100, 100, '2003-02-21', false);
insert into tokens (id, user_id, token, expiry_date, used) values (101,100, 101, '2077-02-21', true);

insert into team (id, name, owner_id) values (100, 'Grupo 14', 100);
insert into team (id, name, owner_id) values (101, 'Gruppe 14', 100);

insert into team_member (user_id, team_id) values (100, 100);
insert into team_member (user_id, team_id) values (101, 100);
insert into team_member (user_id, team_id) values (100, 101);

insert into participant (id, user_id, tournament_id, points, group_number, score_difference) values (100,100,100, 7, 1, 6);
insert into participant (id, user_id, tournament_id, points, score_difference) values (101,100,101, 7, 6);
insert into participant (id, user_id, tournament_id, points) values (102,100,102, 0);
insert into participant (id, user_id, tournament_id, points) values (103,100,103, 0);
insert into participant (id, user_id, tournament_id, points) values (104,100,104, 0);
insert into participant (id, user_id, tournament_id, points) values (105,100,105, 0);
insert into participant (id, user_id, tournament_id, points) values (106,100,106, 0);
insert into participant (id, user_id, tournament_id, points) values (107,100,107, 0);
insert into participant (id, user_id, tournament_id, points) values (108,100,108, 0);
insert into participant (id, user_id, tournament_id, points) values (109,100,109, 0);
insert into participant (id, user_id, tournament_id, points) values (110,100,110, 0);
insert into participant (id, user_id, tournament_id, points) values (111,100,111, 0);
insert into participant (id, user_id, tournament_id, points) values (112,100,112, 0);
insert into participant (id, user_id, tournament_id, points) values (113,100,113, 0);
insert into participant (id, user_id, tournament_id, points) values (114,100,114, 0);
insert into participant (id, user_id, tournament_id, points) values (115,100,115, 0);
insert into participant (id, user_id, tournament_id, points) values (116,100,116, 0);
insert into participant (id, user_id, tournament_id, points) values (117,100,117, 0);
insert into participant (id, user_id, tournament_id, points) values (118,100,118, 0);
insert into participant (id, user_id, tournament_id, points) values (119,100,119, 0);
insert into participant (id, team_id, tournament_id, points, group_number, score_difference) values (120,100,100, 7,1, 6);
insert into participant (id, team_id, tournament_id, points,score_difference) values (121,100,101, 7,6);
insert into participant (id, team_id, tournament_id, points) values (122,100,102, 0);
insert into participant (id, team_id, tournament_id, points) values (123,100,103, 0);
insert into participant (id, team_id, tournament_id, points) values (124,100,104, 0);
insert into participant (id, team_id, tournament_id, points) values (125,100,105, 0);
insert into participant (id, team_id, tournament_id, points) values (126,100,106, 0);
insert into participant (id, team_id, tournament_id, points) values (127,100,107, 0);
insert into participant (id, team_id, tournament_id, points) values (128,100,108, 0);
insert into participant (id, team_id, tournament_id, points) values (129,100,109, 0);
insert into participant (id, team_id, tournament_id, points) values (130,100,110, 0);
insert into participant (id, team_id, tournament_id, points) values (131,100,111, 0);
insert into participant (id, team_id, tournament_id, points) values (132,100,112, 0);
insert into participant (id, team_id, tournament_id, points) values (133,100,113, 0);
insert into participant (id, team_id, tournament_id, points) values (134,100,114, 0);
insert into participant (id, team_id, tournament_id, points) values (135,100,115, 0);
insert into participant (id, team_id, tournament_id, points) values (136,100,116, 0);
insert into participant (id, team_id, tournament_id, points) values (137,100,117, 0);
insert into participant (id, team_id, tournament_id, points) values (138,100,118, 0);
insert into participant (id, team_id, tournament_id, points) values (139,100,119, 0);
insert into participant (id, user_id, tournament_id, points) values (140,101,120, 0);
insert into participant (id, team_id, tournament_id, points) values (141,101,120, 0);
insert into participant (id, user_id, tournament_id, points, group_number, score_difference) values (142,101,100, 1, 1, -6);
insert into participant (id, team_id, tournament_id, points) values (143,101,100, 0);

insert into game_format (id, name, players_per_team, game_id) values (100, 'formi', 6, 103);

insert into match (id, tournament_id, local_id, visitor_id, winner, local_score, visitor_score, is_group_stage, stage)
    VALUES (0, 100, 100, 101, 2, 4, 5, true, 1);
insert into match (id, tournament_id, local_id, visitor_id, winner, local_score, visitor_score, is_group_stage, stage)
    VALUES (1, 100, 100, 102, 2, 4, 5, true, 2);
insert into match (id, tournament_id, local_id, visitor_id, winner, local_score, visitor_score, is_group_stage, stage)
    VALUES (2, 100, 100, 103, 2, 4, 5, true, 3);
insert into match (id, tournament_id, local_id, visitor_id, winner, local_score, visitor_score, is_group_stage, stage)
    VALUES (3, 100, 101, 102, 2, 4, 5, true, 3);
insert into match (id, tournament_id, local_id, visitor_id, winner, local_score, visitor_score, is_group_stage, stage)
    VALUES (4, 100, 101, 103, 2, 4, 5, true, 2);
insert into match (id, tournament_id, local_id, visitor_id, winner, local_score, visitor_score, is_group_stage, stage)
    VALUES (5, 100, 102, 103, 2, 4, 5, true, 1);

insert into match (id, tournament_id, is_group_stage)
    VALUES (0, 102, true);

insert into comments (id, commenter_id, receiver_id, comment, created_at)
    values (100, 101, 100, 'First','2025-11-16 22:04:01');
insert into comments (id, commenter_id, receiver_id, comment, created_at)
values (101, 101, 100, 'First','2025-11-16 22:04:02');
insert into comments (id, commenter_id, receiver_id, comment, created_at)
values (102, 101, 100, 'First','2025-11-16 22:04:03');
insert into comments (id, commenter_id, receiver_id, comment, created_at)
values (103, 101, 100, 'First','2025-11-16 22:04:04');
insert into comments (id, commenter_id, receiver_id, comment, created_at)
values (104, 101, 100, 'First','2025-11-16 22:04:05');
insert into comments (id, commenter_id, receiver_id, comment, created_at)
values (105, 101, 100, 'First','2025-11-16 22:04:06');
insert into comments (id, commenter_id, receiver_id, comment, created_at)
values (106, 101, 100, 'First','2025-11-16 22:04:07');
