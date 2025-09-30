package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.Tournament.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
            rs.getString("format"), Structure.valueOf(rs.getString("structure")), rs.getInt("max_participants"), rs.getLong("image_id"),
            rs.getBoolean("open_inscriptions"), rs.getBoolean("is_finished"), rs.getLong("tournament_winner"), rs.getBoolean("is_group_stage"), rs.getBoolean("tournament_started"));

    private static final RowMapper<User> ROW_MAPPER_USER = (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getBoolean("verified"));

    private static final RowMapper<ParticipantUser> ROW_MAPPER_PARTICIPANT_USER = (rs, rowNum) -> {
        ParticipantUser participant = new ParticipantUser(rs.getLong("user_id"), rs.getLong("tournament_id"), rs.getInt("group_number"));
        participant.setPoints(rs.getInt("points"));
        return participant;
    };

    public static final RowMapper<Match> ROW_MAPPER_MATCH = (rs, rowNum) -> new Match(
            rs.getLong("id"),
            rs.getLong("tournament_id"),
            rs.getObject("local_id") != null ? rs.getLong("local_id") : null,
            rs.getObject("visitor_id") != null ? rs.getLong("visitor_id") : null,
            rs.getObject("local_score") != null ? rs.getInt("local_score") : null,
            rs.getObject("visitor_score") != null ? rs.getInt("visitor_score") : null,
            rs.getObject("winner") != null ? rs.getInt("winner") : null,
            rs.getInt("stage")
    );

    @Override
    public Optional<Tournament> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM tournament WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Tournament> findGameTournaments(Long game_id) {
        return jdbcTemplate.query("SELECT * FROM tournament t WHERE game_id = ? AND open_inscriptions = true", ROW_MAPPER, game_id);
    }

    @Override
    public Tournament create(Long creator_id, String name, Long game_id, Region region, Elo elo, LocalDate start_date, LocalDate end_date, String format, Structure structure, Integer max_participants, byte[] image, Boolean open_inscriptions, Boolean is_finished) {

        SqlParameterSource img = new MapSqlParameterSource().addValue("image", image);
        Long image_id = jdbcInsertImage.executeAndReturnKey(img).longValue();

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
                .addValue("is_finished", is_finished)
                .addValue("tournament_started", false);

        Number key = jdbcInsert.executeAndReturnKey(values);
        createMatches(key.longValue());
        return new Tournament(key.longValue(), creator_id, name, game_id, region, elo, start_date, end_date, format, structure, max_participants, image_id, open_inscriptions, is_finished, null, null, false);
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
        return jdbcTemplate.query("SELECT u.id, u.username, u.email, u.password, u.verified FROM users u " +
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
                createMatchesBracket(t, participants, 1L);
            } else if (t.getStructure().equals(Structure.HYBRID)) {
                createMatchesHybrid(t, participants);
            } else {
                createMatchesLeague(t, participants);
            }
        }
    }

    public void createMatchesLeague(Tournament t, List<ParticipantUser> participants) {
        createMatchesLeague(t, participants, 1);
    }

    public void createMatchesLeague(Tournament t, List<ParticipantUser> participants, int firstMatchId) {

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
                    jdbcInsertMatch.execute(values);
                }
            }
            ParticipantUser first = rotated.remove(1);
            rotated.add(first);
        }
    }

    public void createMatchesBracket(Tournament t, List<ParticipantUser> participants, Long firstMatchId) {
        int n = participants.size();

        int floorPowerOfTwo = 1;
        while (floorPowerOfTwo * 2 <= n) {
            floorPowerOfTwo *= 2;
        }
        int extras = n - floorPowerOfTwo;

        Long matchId = firstMatchId;
        int stage = 1;

        List<ParticipantUser> nextRound = new ArrayList<>();

        for (int i = 0; i < extras; i++) {
            ParticipantUser home = participants.get(i * 2);
            ParticipantUser away = participants.get(i * 2 + 1);

            Map<String, Object> values = new HashMap<>();
            values.put("id", matchId++);
            values.put("tournament_id", t.getId());
            values.put("local_id", home.getUser_id());
            values.put("visitor_id", away.getUser_id());
            values.put("local_score", null);
            values.put("visitor_score", null);
            values.put("winner", null);
            values.put("stage", stage);
            jdbcInsertMatch.execute(values);

            nextRound.add(null);
        }

        for (int i = extras * 2; i < n; i++) {
            nextRound.add(participants.get(i));
        }

        stage++;

        while (nextRound.size() > 1) {
            List<ParticipantUser> currentRound = nextRound;
            nextRound = new ArrayList<>();

            for (int i = 0; i < currentRound.size(); i += 2) {
                ParticipantUser home = currentRound.get(i);
                ParticipantUser away = (i + 1 < currentRound.size()) ? currentRound.get(i + 1) : null;

                Map<String, Object> values = new HashMap<>();
                values.put("id", matchId++);
                values.put("tournament_id", t.getId());
                values.put("local_id", home != null ? home.getUser_id() : null);
                values.put("visitor_id", away != null ? away.getUser_id() : null);
                values.put("local_score", null);
                values.put("visitor_score", null);
                values.put("winner", null);
                values.put("stage", stage);
                jdbcInsertMatch.execute(values);

                nextRound.add(null);
            }

            stage++;
        }
    }

    public void createMatchesHybrid(Tournament t, List<ParticipantUser> participants) {
        int n = participants.size();
        if (n > 8){
            jdbcTemplate.update("UPDATE tournament SET is_group_stage = true WHERE id = ?", t.getId());
            int groupsCount = calculateGroups(n);
            List<Integer> distribution = distributeParticipants(n);
            int index = 0;
            for (int g = 0; g < groupsCount; g++) {
                int size = distribution.get(g);
                List<ParticipantUser> group = new ArrayList<>(participants.subList(index, index + size));
                Long[] ids = group.stream().map(ParticipantUser::getUser_id).toArray(Long[]::new);
                int groupNumber = g + 1;
                jdbcTemplate.update(con -> {
                    Array arr = con.createArrayOf("bigint", ids);
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE participant_user " +
                                    "SET group_number = ? " +
                                    "WHERE tournament_id = ? AND user_id = ANY(?)"
                    );
                    ps.setInt(1, groupNumber);
                    ps.setLong(2, t.getId());
                    ps.setArray(3, arr);
                    return ps;
                });

                index += size;
            }
        }else{
            jdbcTemplate.update("UPDATE tournament SET is_group_stage = false WHERE id = ?", t.getId());
            createMatchesBracket(t, participants, 1L);
        }
    }

    private void createBracketFromGroups(Long tournamentId) {
        Tournament t = findById(tournamentId).orElse(null);
        if (t == null || !t.getStructure().equals(Structure.HYBRID)) {
            return;
        }

        final String sql = """
            WITH ranked AS (
              SELECT pu.user_id,
                     pu.group_number,
                     ROW_NUMBER() OVER (
                       PARTITION BY pu.group_number
                       ORDER BY pu.points DESC, pu.user_id
                     ) AS rk
              FROM participant_user pu
              WHERE pu.tournament_id = ? AND pu.group_number IS NOT NULL
            )
            SELECT group_number,
                   MAX(CASE WHEN rk = 1 THEN user_id END) AS first_id,
                   MAX(CASE WHEN rk = 2 THEN user_id END) AS second_id
            FROM ranked
            GROUP BY group_number
            ORDER BY group_number
        """;

        List<Map<String, Object>> pairs = jdbcTemplate.queryForList(sql, tournamentId);
        int g = pairs.size();
        if (g == 0) {
            return;
        }

        List<ParticipantUser> classified = new ArrayList<>(g * 2);
        for (int i = 0; i < g; i++) {
            Long firstId  = ((Number) pairs.get(i).get("first_id")).longValue();
            Long secondId = ((Number) pairs.get((i + 1) % g).get("second_id")).longValue();

            ParticipantUser local   = getTournamentParticipantByUserId(tournamentId, firstId);
            ParticipantUser visitor = getTournamentParticipantByUserId(tournamentId, secondId);

            classified.add(local);
            classified.add(visitor);
        }

        Long maxId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id), 0) FROM match WHERE tournament_id = ?",
                Long.class, tournamentId
        );

        jdbcTemplate.update("UPDATE tournament SET is_group_stage = false WHERE id = ?", t.getId());
        createMatchesBracket(t, classified, maxId + 1);
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
    public List<MatchWithPlayers> getTournamentMatches(Long tournament_id) {
        String sql = "SELECT m.id, m.tournament_id, m.local_id, m.visitor_id, " +
                    "COALESCE(local_user.username, 'TBD') as local_player_name, " +
                    "COALESCE(visitor_user.username, 'TBD') as visitor_player_name, " +
                    "m.local_score, m.visitor_score, m.winner, m.stage " +
                    "FROM match m " +
                    "LEFT JOIN users local_user ON m.local_id = local_user.id " +
                    "LEFT JOIN users visitor_user ON m.visitor_id = visitor_user.id " +
                    "WHERE m.tournament_id = ? " +
                    "ORDER BY m.stage, m.id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new MatchWithPlayers(
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
    }

    @Override
    public List<Tournament> findByCreator(Long creator_id) {
        return jdbcTemplate.query("SELECT * FROM tournament t WHERE creator_id = ?", ROW_MAPPER, creator_id);
    }

    @Override
    public void setFinished(Long tournament_id, Long match_id) {
        jdbcTemplate.update("UPDATE tournament SET is_finished = true WHERE id = ?", tournament_id);
        if(match_id != null){
            setTournamentWinner(tournament_id, match_id);
        }
    }

    @Override
    public void closeInscriptions(Long tournament_id) {
        jdbcTemplate.update("UPDATE tournament SET open_inscriptions = false WHERE id = ?", tournament_id);
        createMatches(tournament_id);
    }

    @Override
    public void startTournament(Long tournament_id) {
        jdbcTemplate.update("UPDATE tournament SET tournament_started = true WHERE id = ?", tournament_id);
        Tournament t = findById(tournament_id).orElse(null);
        if(t != null && t.getStructure().equals(Structure.HYBRID)){
            Map<Integer, List<ParticipantUser>> groupedParticipants = getGroupedParticipants(tournament_id);
            if(groupedParticipants != null){
                int nextId = 1;
                for(List<ParticipantUser> participants : groupedParticipants.values()){
                    nextId += (participants.size() * (participants.size() - 1)) / 2;
                    createMatchesLeague(t, participants, nextId);
                }
            }
        }
    }

    private Map<Integer, List<ParticipantUser>> getGroupedParticipants(Long tournamentId) {
        List<ParticipantUser> participantUsers = getTournamentParticipantUsers(tournamentId);

        Map<Integer, List<ParticipantUser>> grouped = new TreeMap<>(Integer::compareTo);

        for (ParticipantUser p : participantUsers) {
            if (p.getGroupNumber() == null) {
                return null;
            }
            grouped.computeIfAbsent(p.getGroupNumber(), k -> new ArrayList<>()).add(p);
        }
        return grouped;
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
        if (!isTournamentStarted(tournamentId)) {
            throw new IllegalStateException("Results cannot be set before the tournament starts");
        }

        if (winner == null || (winner != 1 && winner != 2)) {
            throw new IllegalArgumentException("winner must be 1 (local) or 2 (visitor)");
        }

        Map<String, Object> match = jdbcTemplate.queryForMap(
                "SELECT local_id, visitor_id FROM match WHERE id = ? AND tournament_id = ?",
                matchId, tournamentId
        );

        Long localId = (Long) match.get("local_id");
        Long visitorId = (Long) match.get("visitor_id");

        if (localId == null || visitorId == null) {
            throw new IllegalStateException("Cannot set winner for TBD matches");
        }

        jdbcTemplate.update("UPDATE match SET winner = ? WHERE id = ? AND tournament_id = ?",
                winner, matchId, tournamentId);

        Long winnerId = (winner == 1) ? localId : visitorId;

        Integer totalMatches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM match WHERE tournament_id = ?",
                Integer.class, tournamentId
        );

        Integer finishedMatches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM match WHERE tournament_id = ? AND winner IS NOT NULL AND winner > 0",
                Integer.class, tournamentId
        );

        boolean isFinished = totalMatches.equals(finishedMatches) && totalMatches > 0;

        Tournament t = findById(tournamentId).orElse(null);
        if (t == null) {
            return;
        }
        Structure structure = t.getStructure();
        boolean isGroupStage = Boolean.TRUE.equals(t.getIs_group_stage());

        if(structure.equals(Structure.ELIMINATION) && !isFinished) {
            setNextMatchInfo(matchId, tournamentId, winnerId);
        }else if(structure.equals(Structure.HYBRID)) {
            if(isGroupStage) {
                jdbcTemplate.update("UPDATE participant_user SET points = points + 3 WHERE user_id = ?", winnerId);
                if(isFinished) {
                    createBracketFromGroups(tournamentId);
                    totalMatches = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM match WHERE tournament_id = ?",
                            Integer.class, tournamentId
                    );
                    isFinished = totalMatches.equals(finishedMatches);
                }
            }else if(!isFinished){
                setNextMatchInfo(matchId, tournamentId, winnerId);
            }
        }else{
            jdbcTemplate.update("UPDATE participant_user SET points = points + 3 WHERE user_id = ?", winnerId);
        }

        if (isFinished) {
            setFinished(tournamentId, matchId);
        }
    }


    private void setNextMatchInfo(Long matchId, Long tournamentId, Long winnerId) {
        Integer currentStage = jdbcTemplate.queryForObject(
                "SELECT stage FROM match WHERE id = ? AND tournament_id = ?",
                Integer.class, matchId, tournamentId
        );

        List<Long> idsThisStage = jdbcTemplate.queryForList(
                "SELECT id FROM match WHERE tournament_id = ? AND stage = ? ORDER BY id",
                Long.class, tournamentId, currentStage
        );

        int indexInStage = idsThisStage.indexOf(matchId);
        if (indexInStage < 0) {
            return;
        }
        int parentOffset = indexInStage / 2;
        List<Long> nextMatches = jdbcTemplate.queryForList(
                "SELECT id FROM match WHERE tournament_id = ? AND stage = ? ORDER BY id LIMIT 1 OFFSET ?",
                Long.class, tournamentId, currentStage + 1, parentOffset
        );

        if (nextMatches.isEmpty()) {
            return;
        }
        Long nextMatchId = nextMatches.get(0);

        boolean isLeftChild = (indexInStage % 2 == 0);

        if (isLeftChild) {
            jdbcTemplate.update(
                    "UPDATE match SET local_id = ? WHERE id = ? AND tournament_id = ?",
                    winnerId, nextMatchId, tournamentId
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE match SET visitor_id = ? WHERE id = ? AND tournament_id = ?",
                    winnerId, nextMatchId, tournamentId
            );
        }
    }

    @Override
    public List<Tournament> findUserActiveTournaments(Long userId) {
        return findUserTournaments(userId, false);
    }

    @Override
    public List<Tournament> findUserPastTournaments(Long userId) {
        return findUserTournaments(userId, true);
    }

    private List<Tournament> findUserTournaments(Long userId, Boolean isFinished) {
        String sql = "SELECT t.* " +
                "FROM tournament t " +
                "INNER JOIN participant_user p ON p.tournament_id = t.id " +
                "WHERE p.user_id = ? AND t.is_finished = ?; ";
        return jdbcTemplate.query(sql, ROW_MAPPER, userId, isFinished);
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter filter) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = "SELECT * FROM tournament t" + buildTournamentFilterSql(filter, params);

        return namedJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    private String buildTournamentFilterSql(TournamentFilter filter, MapSqlParameterSource params) {

        StringBuilder sql = new StringBuilder();

        boolean needsGameJoin = filter.getGenre() != null;
        boolean needsFormatJoin = filter.getPlayersPerTeam() != null;

        if (needsGameJoin) {
            sql.append(" JOIN game g ON t.game_id = g.id");
        }
        if (needsFormatJoin) {
            sql.append(" LEFT JOIN game_format gf ON t.game_id = gf.game_id");
        }

        sql.append(" WHERE open_inscriptions = true");

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
        if (filter.getPlayersPerTeam() != null) {
            sql.append(" AND gf.players_per_team = :playersPerTeam");
            params.addValue("playersPerTeam", filter.getPlayersPerTeam());
        }
        if (filter.getGenre() != null) {
            sql.append(" AND g.genre = :genre");
            params.addValue("genre", filter.getGenre(), Types.OTHER);
        }
        return sql.toString();
    }

    public List<Tournament> searchByName(String name){
        String sql = "SELECT * " +
                "FROM tournament " +
                "WHERE LOWER(name) LIKE '%' || LOWER(?) || '%'";

        return jdbcTemplate.query(sql, ROW_MAPPER, name);
    }

    private void setTournamentWinner(Long tournamentId, Long matchId) {
        Tournament t = findById(tournamentId).orElse(null);
        if (t == null || !t.getFinished()) {
            return ;
        }

        Long winner = null;

        if (t.getStructure() == Structure.LEAGUE) {
            Integer maxPoints = jdbcTemplate.queryForObject(
                    "SELECT MAX(points) FROM participant_user WHERE tournament_id = ?",
                    Integer.class, tournamentId
            );

            winner = jdbcTemplate.queryForObject(
                    "SELECT MAX(pu.user_id) " +
                            "FROM participant_user pu " +
                            "WHERE pu.tournament_id = ? AND pu.points = ?",
                    (rs, rowNum) -> rs.getLong("user_id"),
                    tournamentId, maxPoints
            );
        } else {
            Match finalMatch = getMatch(tournamentId, matchId);

            if (finalMatch != null && finalMatch.getWinner() != null &&  finalMatch.getWinner() > 0) {
                winner = (finalMatch.getWinner() == 1)
                        ? finalMatch.getLocalId()
                        : finalMatch.getVisitorId();
            }
        }
        jdbcTemplate.update("UPDATE tournament SET tournament_winner = ? WHERE id = ?", winner, tournamentId);
    }

    @Override
    public Map<Long, Integer> getTournamentGroupsByUser(Long tournamentId) {
        final String sql = """
            SELECT user_id, group_number
            FROM participant_user
            WHERE tournament_id = ?
        """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, tournamentId);
        return rows.stream().collect(Collectors.toMap(
                r -> ((Number) r.get("user_id")).longValue(),
                r -> r.get("group_number") == null ? 0 : ((Number) r.get("group_number")).intValue()
        ));
    }

    private Integer getGroupNumber(long tournamentId, long userId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT group_number FROM participant_user WHERE tournament_id=? AND user_id=?",
                    Integer.class, tournamentId, userId
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Match getMatch(long tournamentId, long matchId) {
         return jdbcTemplate.queryForObject(
                "SELECT * FROM match WHERE tournament_id = ? AND id = ?",
                ROW_MAPPER_MATCH, tournamentId, matchId
        );
    }

    private boolean isTournamentStarted(Long tournamentId) {
        return jdbcTemplate.queryForObject(
                "SELECT tournament_started FROM tournament WHERE id = ?",
                Boolean.class, tournamentId
        );
    }

    @Override
    @Transactional
    public void swapGroups(Long tournament_id, Long user1, Long user2){
        if (isTournamentStarted(tournament_id)) {
            throw new IllegalStateException("Members cannot be swapped after the tournament has started");
        }

        Integer g1 = getGroupNumber(tournament_id, user1);
        Integer g2 = getGroupNumber(tournament_id, user2);

        if (g1 == null || g2 == null) {
            throw new IllegalArgumentException("Both users must exist in the tournament");
        }
        if (g1.equals(g2)) {
            return;
        }

        jdbcTemplate.update(
                "UPDATE participant_user " +
                        "SET group_number = CASE " +
                        "  WHEN user_id = ? THEN ? " +
                        "  WHEN user_id = ? THEN ? " +
                        "  ELSE group_number END " +
                        "WHERE tournament_id = ? AND user_id IN (?, ?)",
                user1, g2,
                user2, g1,
                tournament_id, user1, user2
        );
    }

    @Override
    @Transactional
    public void swapMatchesMembers(Long tournament_id, Long match1, Long match2, Long user1, Long user2){
        if (isTournamentStarted(tournament_id)) {
            throw new IllegalStateException("Members cannot be swapped after the tournament has started");
        }

        Match m1 = getMatch(tournament_id, match1);
        Match m2 = getMatch(tournament_id, match2);
        if (m1 == null || m2 == null) {
            throw new IllegalArgumentException("Both matches must exist in the tournament");
        }

        boolean u1IsLocalM1 = m1.getLocalId() != null && m1.getLocalId().equals(user1);
        boolean u1IsVisitM1 = m1.getVisitorId() != null && m1.getVisitorId().equals(user1);
        boolean u2IsLocalM2 = m2.getLocalId() != null && m2.getLocalId().equals(user2);
        boolean u2IsVisitM2 = m2.getVisitorId() != null && m2.getVisitorId().equals(user2);

        if ((!u1IsLocalM1 && !u1IsVisitM1) || (!u2IsLocalM2 && !u2IsVisitM2)) {
            throw new IllegalArgumentException("user1 must be in match1 and user2 must be in match2");
        }

        if (u1IsLocalM1) {
            jdbcTemplate.update(
                    "UPDATE match SET local_id=? WHERE tournament_id=? AND id=?",
                    user2, tournament_id, match1
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE match SET visitor_id=? WHERE tournament_id=? AND id=?",
                    user2, tournament_id, match1
            );
        }
        if (u2IsLocalM2) {
            jdbcTemplate.update(
                    "UPDATE match SET local_id=? WHERE tournament_id=? AND id=?",
                    user1, tournament_id, match2
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE match SET visitor_id=? WHERE tournament_id=? AND id=?",
                    user1, tournament_id, match2
            );
        }
    }

    @Override
    public void updateTournamentInfo(Long tournamentId, String name, LocalDate startDate, LocalDate endDate, Integer maxParticipants) {
        List<String> sets = new ArrayList<>();
        MapSqlParameterSource p = new MapSqlParameterSource().addValue("id", tournamentId);

        if (name != null) {
            sets.add("name = :name");
            p.addValue("name", name);
        }
        if (startDate != null) {
            sets.add("start_date = :start_date");
            p.addValue("start_date", startDate);
        }
        if (endDate != null) {
            sets.add("end_date = :end_date");
            p.addValue("end_date", endDate);
        }
        if (maxParticipants != null) {
            sets.add("max_participants = :max_participants");
            p.addValue("max_participants", maxParticipants);
        }

        if (sets.isEmpty()) {
            return;
        }

        String sql = "UPDATE tournament SET " + String.join(", ", sets) + " WHERE id = :id";
        namedJdbcTemplate.update(sql, p);
    }

    @Override
    public int tournamentParticipantsCount(Long tournamentId){
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM participant_user WHERE tournament_id = ?",
                Integer.class, tournamentId
        );
    }
}
