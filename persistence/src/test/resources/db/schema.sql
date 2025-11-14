set database sql syntax pgs true;
set ignorecase true;

insert into users (id, email, username, password, verified, locale)
    values (100, 'another@mail.com','janedoe','1234567890',false,'es');
insert into users (id, email, username, password, verified, locale)
values (101, 'another2@mail.com','janetdoe','1234567890',false,'es');

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
    (100, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (101, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (102, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (103, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (104, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (105, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (106, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (107, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (108, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (109, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (110, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (111, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (112, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (113, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (114, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (115, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (116, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (117, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (118, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, true, false,true, false,100,100);
insert into tournament (id, creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, is_group_stage, tournament_started, format_id, rules_id) values
    (119, 100, 'Jerma Rumble', 100, 'LAS', 'LOW', '2026-02-21', '2027-02-21', 'some format', 'LEAGUE', 4, 100, false, true,true, false,100,100);

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

insert into participant (id, user_id, tournament_id) values (100,100,100);
insert into participant (id, team_id, tournament_id) values (101,100,100);
insert into participant (id, team_id, tournament_id) values (102,100,101);
insert into participant (id, team_id, tournament_id) values (103,100,102);
insert into participant (id, team_id, tournament_id) values (104,100,103);
insert into participant (id, team_id, tournament_id) values (105,100,104);
insert into participant (id, team_id, tournament_id) values (106,100,105);
insert into participant (id, team_id, tournament_id) values (107,100,106);
insert into participant (id, team_id, tournament_id) values (108,100,107);
insert into participant (id, team_id, tournament_id) values (109,100,108);
insert into participant (id, team_id, tournament_id) values (110,100,109);
insert into participant (id, team_id, tournament_id) values (111,100,110);
insert into participant (id, team_id, tournament_id) values (112,100,111);
insert into participant (id, team_id, tournament_id) values (113,100,112);
insert into participant (id, team_id, tournament_id) values (114,100,113);
insert into participant (id, team_id, tournament_id) values (115,100,114);
insert into participant (id, team_id, tournament_id) values (116,100,115);
insert into participant (id, team_id, tournament_id) values (117,100,116);
insert into participant (id, team_id, tournament_id) values (118,100,117);
insert into participant (id, team_id, tournament_id) values (119,100,118);
insert into participant (id, team_id, tournament_id) values (120,100,119);