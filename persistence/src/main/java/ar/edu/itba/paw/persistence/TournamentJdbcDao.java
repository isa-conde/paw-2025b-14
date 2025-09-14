package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.Tournament.TournamentImg;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

@Repository
public class TournamentJdbcDao implements TournamentDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertParticipantUser;
    private final SimpleJdbcInsert jdbcInsertMatch;
    //private final SimpleJdbcInsert jdbcInsertTeam;
    private final SimpleJdbcInsert jdbcInsertImage;

    @Autowired
    public TournamentJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament")
                .usingGeneratedKeyColumns("id");
        this.jdbcInsertParticipantUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant_user");
        //this.jdbcInsertTeam = new SimpleJdbcInsert(jdbcTemplate)
        //        .withTableName("participant_team");
        this.jdbcInsertMatch = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("match");
        this.jdbcInsertImage = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("image")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Tournament> ROW_MAPPER = (rs, rowNum) -> new Tournament(rs.getLong("id"),
            rs.getLong("creator_id"), rs.getString("name"), rs.getLong("game_id"), Region.valueOf(rs.getString("region")),
            Elo.valueOf(rs.getString("elo")), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
            rs.getString("format"), Structure.valueOf(rs.getString("structure")), rs.getInt("max_participants"), rs.getInt("image_id"),
            rs.getBoolean("open_inscriptions"), rs.getBoolean("is_finished"));

    private static final RowMapper<User> ROW_MAPPER_USER = (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"));

    private static final RowMapper<ParticipantUser> ROW_MAPPER_PARTICIPANT_USER = (rs, rowNum) -> {
        ParticipantUser participant = new ParticipantUser(rs.getLong("user_id"), rs.getLong("tournament_id"));
        participant.setPoints(rs.getInt("points"));
        return participant;
    };

    private static final RowMapper<TournamentImg> ROW_MAPPER_IMG = (rs, rowNum) -> new TournamentImg(new Tournament(rs.getLong("id"),
            rs.getLong("creator_id"), rs.getString("name"), rs.getLong("game_id"), Region.valueOf(rs.getString("region")),
            Elo.valueOf(rs.getString("elo")), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
            rs.getString("format"), Structure.valueOf(rs.getString("structure")), rs.getInt("max_participants"), rs.getInt("image_id"),
            rs.getBoolean("open_inscriptions"), rs.getBoolean("is_finished")), Base64.getEncoder().encodeToString(rs.getBytes("image")));

    @Override
    public Optional<Tournament> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM tournament WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Tournament> findGameTournaments(Long game_id){
        return jdbcTemplate.query("SELECT * FROM tournament t WHERE game_id = ? AND open_inscriptions = true", ROW_MAPPER, game_id);
    }

    @Override
    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image, Boolean open_inscriptions, Boolean is_finished) {

        SqlParameterSource img = new MapSqlParameterSource().addValue("image", image);
        Integer image_id = jdbcInsertImage.executeAndReturnKey(img).intValue();

        SqlParameterSource values = new MapSqlParameterSource()
                .addValue("creator_id", creator_id)
                .addValue("name", name)
                .addValue("game_id", game_id)
                .addValue("region", region, Types.OTHER)
                .addValue("elo", elo, Types.OTHER)
                .addValue("start_date", start_date)
                .addValue("end_date", end_date)
                .addValue("format", format)
                .addValue("structure", structure.name(), Types.OTHER)
                .addValue("max_participants", max_participants)
                .addValue("image_id", image_id)
                .addValue("open_inscriptions", open_inscriptions)
                .addValue("is_finished", is_finished);

        Number key = jdbcInsert.executeAndReturnKey(values);
        createMatches(key.longValue());
        return new Tournament(key.longValue(), creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished);
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        Map<String, Object> values = new HashMap<>();

        values.put("user_id", user_id);
        values.put("tournament_id", tournament_id);
        values.put("points", 0);

        jdbcInsertParticipantUser.execute(values);

        List<ParticipantUser> participantUsers = getTournamentParticipantUsers(tournament_id);
        Optional<Tournament> tournament = findById(tournament_id);
        if (tournament.isPresent() && participantUsers.size() == tournament.get().getMax_participants()) {
            closeInscriptions(tournament_id);
        }
    }
    
    public int getCurrentParticipants(Long tournament_id) {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM participant_user WHERE tournament_id = ?", Integer.class, tournament_id);
    }

	@Override
    public void loadScores(Long match_id, Long tournament_id, Integer local_score, Integer visitor_score) {
		jdbcTemplate.update("UPDATE match SET local_score = ?, visitor_score = ? WHERE id = ? AND tournament_id = ?", local_score, visitor_score, match_id, tournament_id);
	}

    /*
    @Override
    public void joinTournamentTeam(Long team_id, Long tournament_id) {
        Map<String, Object> values = new HashMap<>();

        values.put("team_id", team_id);
        values.put("tournament_id", tournament_id);

        jdbcInsertParticipant.execute(values);
    }
    */
    @Override
    public List<User> getTournamentUsers(Long tournament_id) {
        return jdbcTemplate.query("SELECT u.id, u.username, u.email FROM users u " +
                "JOIN participant_user p ON u.id = p.user_id " +
                "WHERE p.tournament_id = ?", ROW_MAPPER_USER, tournament_id);
    }

    @Override
    public Optional<Structure> getTournamentStructure(Long tournament_id) {
    	Tournament t = findById(tournament_id).orElse(null);
    	if (t != null) {
			return Optional.of(t.getStructure());
		}
    	return Optional.empty();
	}

    @Override
    public void createMatches(Long tournamentId) {
        Tournament t = findById(tournamentId).orElse(null);
        List<ParticipantUser> participants = getTournamentParticipantUsers(tournamentId);

        if (t != null && !participants.isEmpty()) {
            if(t.getStructure().equals(Structure.ELIMINATION)) {
                createMatchesBracket(t, participants, 1);
            } else if (t.getStructure().equals(Structure.HYBRID)) {
                createMatchesHybrid(t, participants);
            } else {
                createMatchesLeague(t, participants);
            }
        }
    }

    public void createMatchesLeague(Tournament t, List<ParticipantUser> participants) {
        createMatchesLeague(t, participants, 0, 1);
    }

    public void createMatchesLeague(Tournament t, List<ParticipantUser> participants, Integer groupNumber, int firstMatchId) {

        int n = participants.size();

        // Odd # of participants -> add fictional participant
        if (n % 2 != 0) {
            participants.add(null);
            n++;
        }
        int totalRounds = n - 1;
        int matchId = firstMatchId;

        List<ParticipantUser> rotated = new ArrayList<>(participants);

        for (int round = 1; round <= totalRounds; round++) {
            for (int i = 0; i < n / 2; i++) {
                ParticipantUser home = rotated.get(i);
                ParticipantUser away = rotated.get(n - 1 - i);

                if (home != null && away != null) {
                    Map<String, Object> values = new HashMap<>();
                    values.put("id", matchId++);
                    values.put("tournament_id", t.getId());
                    values.put("local_id", home.getUser_id());
                    values.put("visitor_id", away.getUser_id());
                    values.put("local_score", null);
                    values.put("visitor_score", null);
                    values.put("winner", null);
                    values.put("stage", round);
                    values.put("group_number", groupNumber);
                    jdbcInsertMatch.execute(values);
                }
            }
            ParticipantUser first = rotated.remove(1);
            rotated.add(first);
        }
    }

    public void createMatchesBracket(Tournament t, List<ParticipantUser> participants, int firstMatchId) {
         int n = participants.size();

        // Use the closest power of 2 (adding fictional participants)
        int powerOfTwo = 1;
        while (powerOfTwo < n) {
            powerOfTwo *= 2;
        }
        while (participants.size() < powerOfTwo) {
            participants.add(null);
        }

        int totalRounds = (int) (Math.log(participants.size()) / Math.log(2));
        int matchId = firstMatchId;

        int matchesThisRound = participants.size() / 2;
        for (int round = 1; round <= totalRounds; round++) {
            for (int i = 0; i < matchesThisRound; i++) {
                ParticipantUser home = null;
                ParticipantUser away = null;

                if (round == 1) {
                    home = participants.get(2 * i);
                    away = participants.get(2 * i + 1);
                }

                Map<String, Object> values = new HashMap<>();
                values.put("id", matchId++);
                values.put("tournament_id", t.getId());
                values.put("local_id", home != null ? home.getUser_id() : null);
                values.put("visitor_id", away != null ? away.getUser_id() : null);
                values.put("local_score", null);
                values.put("visitor_score", null);
                values.put("winner", null);
                values.put("stage", round);
                jdbcInsertMatch.execute(values);
            }
            matchesThisRound /= 2;
        }
    }

    public void createMatchesHybrid(Tournament t, List<ParticipantUser> participants) {
        int n = participants.size();
        if (n > 16){
            int groupsCount = calculateGroups(n);
            List<Integer> distribution = distributeParticipants(n);
            int index = 0;
            for (int g = 0; g < groupsCount; g++) {
                int size = distribution.get(g);
                List<ParticipantUser> group = new ArrayList<>(participants.subList(index, index + size));
                createMatchesLeague(t, group, g + 1, g * size + 1 );
                index += size;
            }
        }else{
            createMatchesBracket(t, participants, 1);
        }
    }

    public void createBracketFromGroups(Long tournamentId) {
        Tournament t = findById(tournamentId).orElse(null);

        if (t != null && t.getStructure().equals(Structure.HYBRID)) {
            List<Long> firstPlaces = new ArrayList<>();
            List<Long> secondPlaces = new ArrayList<>();

            List<Map<String, Object>> groups = jdbcTemplate.queryForList(
                    "SELECT DISTINCT group_number FROM match WHERE tournament_id = ?",
                    tournamentId
            );

            for (Map<String, Object> g : groups) {
                int groupNum = ((Number) g.get("group_number")).intValue();

                Long first = jdbcTemplate.queryForObject(
                        "SELECT user_id FROM participant_user WHERE tournament_id = ? AND group_number = ? ORDER BY points DESC LIMIT 1",
                        Long.class, tournamentId, groupNum
                );

                Long second = jdbcTemplate.queryForObject(
                        "SELECT user_id FROM participant_user WHERE tournament_id = ? AND group_number = ? ORDER BY points DESC OFFSET 1 LIMIT 1",
                        Long.class, tournamentId, groupNum
                );

                firstPlaces.add(first);
                secondPlaces.add(second);
            }

            List<ParticipantUser> classifiedParticipants = new ArrayList<>();
            for (int i = 0; i < firstPlaces.size(); i++) {
                ParticipantUser local = getTournamentParticipantByUserId(tournamentId, firstPlaces.get(i));
                ParticipantUser visitor = getTournamentParticipantByUserId(tournamentId, secondPlaces.get((i + 1) % secondPlaces.size()));
                classifiedParticipants.add(local);
                classifiedParticipants.add(visitor);
            }

            Integer maxId = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(MAX(id), 0) FROM match WHERE tournament_id = ?",
                    Integer.class, tournamentId
            );

            createMatchesBracket(t, classifiedParticipants, maxId + 1);
        }
    }


    private int calculateGroups(int n) {
        int groups = Math.max(1, n / 3);   // min 3 participants per group
        groups = Math.min(groups, 16);     // máx 16  groups
        return groups;
    }

    private List<Integer> distributeParticipants(int participantsCount) {
        int groups = calculateGroups(participantsCount);

        int baseSize = participantsCount / groups;
        int extra = participantsCount % groups;

        List<Integer> distribution = new ArrayList<>();

        for (int i = 0; i < groups; i++) {
            if (i < extra) {
                distribution.add(baseSize + 1);
            } else {
                distribution.add(baseSize);
            }
        }
        return distribution;
    }


    @Override
    public Map<Integer, List<MatchWithPlayers>> getTournamentMatchesByStage(Long tournament_id) {
        Tournament t = findById(tournament_id).orElse(null);

        String sql = "SELECT m.id, m.tournament_id, m.local_id, m.visitor_id, " +
                    "COALESCE(local_user.username, 'TBD') as local_player_name, " +
                    "COALESCE(visitor_user.username, 'TBD') as visitor_player_name, " +
                    "m.local_score, m.visitor_score, m.winner, m.stage " +
                    "FROM match m " +
                    "LEFT JOIN users local_user ON m.local_id = local_user.id " +
                    "LEFT JOIN users visitor_user ON m.visitor_id = visitor_user.id " +
                    "WHERE m.tournament_id = ? " +
                    "ORDER BY m.stage, m.id";

        List <MatchWithPlayers> matches = jdbcTemplate.query(sql, (rs, rowNum) -> new MatchWithPlayers(
            rs.getLong("id"),
            rs.getLong("tournament_id"),
            rs.getObject("local_id") != null ? rs.getLong("local_id") : null,
            rs.getObject("visitor_id") != null ? rs.getLong("visitor_id") : null,
            rs.getString("local_player_name"),
            rs.getString("visitor_player_name"),
            rs.getObject("local_score") != null ? rs.getInt("local_score") : null,
            rs.getObject("visitor_score") != null ? rs.getInt("visitor_score") : null,
            rs.getObject("winner") != null ? rs.getInt("winner") : null,
            rs.getObject("stage") != null ? rs.getInt("stage") : null
        ), tournament_id);

        Map<Integer, List<MatchWithPlayers>> matchesByStage = new LinkedHashMap<>();
        if (!matches.isEmpty() && t != null) {
            for (MatchWithPlayers match : matches) {
                int stage = match.getStage();
                matchesByStage.computeIfAbsent(stage, k -> new ArrayList<>()).add(match);
            }
        }
        return matchesByStage;
    }

    public Optional<TournamentImg> findByIdWithImg(Long id){
        return jdbcTemplate.query("SELECT t.*, i.image FROM tournament t LEFT JOIN image i ON t.image_id = i.id WHERE t.id = ?", ROW_MAPPER_IMG, id
        ).stream().findFirst();
    }
    @Override
    public List<TournamentImg> findByCreatorImg(Long creator_id) {
        return jdbcTemplate.query("SELECT * FROM tournament t LEFT JOIN image i ON t.image_id = i.id WHERE creator_id = ?", ROW_MAPPER_IMG, creator_id);
    }

    @Override
    public void setFinished(Long tournament_id) {
        jdbcTemplate.update("UPDATE tournament SET is_finished = true WHERE id = ?", tournament_id);
    }

    @Override
    public void closeInscriptions(Long tournament_id){
        jdbcTemplate.update("UPDATE tournament SET open_inscriptions = false WHERE id = ?", tournament_id);
        createMatches(tournament_id);
    }

    @Override
    public List<ParticipantUser> getTournamentParticipantUsers(Long tournament_id) {
        return jdbcTemplate.query("SELECT * FROM participant_user WHERE tournament_id = ?", ROW_MAPPER_PARTICIPANT_USER, tournament_id);
    }

    public ParticipantUser getTournamentParticipantByUserId(Long tournament_id, Long user_id) {
        return jdbcTemplate.query("SELECT * FROM participant_user " +
                "WHERE tournament_id = ? AND user_id = ?"
                , ROW_MAPPER_PARTICIPANT_USER, tournament_id, user_id).stream().findFirst().orElse(null);
    }

    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM participant_user WHERE user_id = ? AND tournament_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId, tournamentId);
    }

    @Override
    public void setMatchWinner(Long matchId, Long tournamentId, Integer winner) {
        jdbcTemplate.update("UPDATE match SET winner = ? WHERE id = ? AND tournament_id = ?",
                           winner, matchId, tournamentId);

        Long winnerId = jdbcTemplate.queryForObject(
                "SELECT CASE WHEN ? = 1 THEN local_id WHEN ? = 2 THEN visitor_id END " +
                        "FROM match WHERE id = ? AND tournament_id = ?",
                Long.class, winner, winner, matchId, tournamentId
        );

        Integer totalMatches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM match WHERE tournament_id = ?",
                Integer.class, tournamentId
        );

        Integer finishedMatches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM match WHERE tournament_id = ? AND winner IS NOT NULL AND winner > 0",
                Integer.class, tournamentId
        );

        boolean isFinished = totalMatches.equals(finishedMatches) && totalMatches > 0;

        Structure structure = getTournamentStructure(tournamentId).isPresent()? getTournamentStructure(tournamentId).get() : null;
        if(structure != null) {
            if(structure.equals(Structure.ELIMINATION) && !isFinished) {
                setNextMatchInfo(matchId, tournamentId, winner);
            }else if(structure.equals(Structure.HYBRID)) {
                Integer group_number = jdbcTemplate.queryForObject(
                        "SELECT group_number FROM match WHERE tournament_id = ? AND match_id = ?",
                        Integer.class, tournamentId, matchId
                );
                if(isFinished && group_number > 0) {
                    createBracketFromGroups(tournamentId);
                    totalMatches = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM match WHERE tournament_id = ?",
                            Integer.class, tournamentId
                    );
                    isFinished = totalMatches.equals(finishedMatches);
                }
            }
            else{
                jdbcTemplate.update("UPDATE participant_user SET points = points + 3 WHERE user_id = ?", winnerId);
            }
        }

        if (isFinished) {
            setFinished(tournamentId);
        }
    }

    public void setNextMatchInfo(Long matchId, Long tournamentId, Integer winnerId) {
        Map<String, Object> match = jdbcTemplate.queryForMap(
                "SELECT stage, id FROM match WHERE id = ? AND tournament_id = ?",
                matchId, tournamentId
        );

        int currentStage = ((Number) match.get("stage")).intValue();

        Map<String, Object> nextMatch = jdbcTemplate.queryForMap(
                "SELECT id FROM match WHERE tournament_id = ? AND stage = ? ORDER BY id LIMIT 1 OFFSET ?",
                tournamentId, currentStage + 1, (matchId - 1) / 2
        );

        Long nextMatchId = ((Number) nextMatch.get("id")).longValue();

        int position = (matchId % 2 == 1) ? 1 : 2;
        if (position == 1) {
            jdbcTemplate.update("UPDATE match SET local_id = ? WHERE id = ? AND tournament_id = ?",
                    winnerId, nextMatchId, tournamentId);
        } else {
            jdbcTemplate.update("UPDATE match SET visitor_id = ? WHERE id = ? AND tournament_id = ?",
                    winnerId, nextMatchId, tournamentId);
        }
    }


    @Override
    public List<TournamentImg> findUserActiveTournaments(Long userId) {
        return findUserTournaments(userId, false);
    }

    @Override
    public List<TournamentImg> findUserPastTournaments(Long userId) {
        return findUserTournaments(userId, true);
    }

    private List<TournamentImg> findUserTournaments(Long userId, Boolean isFinished){
        String sql = "SELECT t.*, i.image " +
                "FROM tournament t " +
                "LEFT JOIN image i ON t.image_id = i.id " +
                "INNER JOIN participant_user p ON p.tournament_id = t.id " +
                "WHERE p.user_id = ? AND t.is_finished = ?; ";
        return jdbcTemplate.query(sql, ROW_MAPPER_IMG, userId, isFinished);
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter filter) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = "SELECT * FROM tournament t" + buildTournamentFilterSql(filter, params);

        return namedJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    @Override
    public List<TournamentImg> findWithImg(TournamentFilter filter) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = "SELECT t.*, i.image " +
                "FROM tournament t " +
                "LEFT JOIN image i ON t.image_id = i.id" +
                buildTournamentFilterSql(filter, params);

        return namedJdbcTemplate.query(sql, params, ROW_MAPPER_IMG);
    }

    private String buildTournamentFilterSql(TournamentFilter filter, MapSqlParameterSource params) {
        StringBuilder sql = new StringBuilder(" WHERE open_inscriptions = true");

        if (filter.getName() != null) {
            sql.append(" AND t.name LIKE :name");
            params.addValue("name", filter.getName());
        }
        if (filter.getGame_id() != null) {
            sql.append(" AND t.game_id = :game_id");
            params.addValue("game_id", filter.getGame_id());
        }
        if (filter.getElo() != null) {
            sql.append(" AND t.elo = :elo");
            params.addValue("elo", filter.getElo(), Types.OTHER);
        }
        if (filter.getRegion() != null) {
            sql.append(" AND t.region = :region");
            params.addValue("region", filter.getRegion(), Types.OTHER);
        }
        if (filter.getFormat() != null) {
            sql.append(" AND t.format = :format");
            params.addValue("format", filter.getFormat());
        }
        if (filter.getStructure() != null) {
            sql.append(" AND t.structure = :structure");
            params.addValue("structure", filter.getStructure(), Types.OTHER);
        }
        if (filter.getStart_date() != null) {
            sql.append(" AND t.start_date < :start_date");
            params.addValue("start_date", filter.getStart_date());
        }
        if (filter.getEnd_date() != null) {
            sql.append(" AND t.end_date < :end_date");
            params.addValue("end_date", filter.getEnd_date());
        }

        return sql.toString();
    }


}
