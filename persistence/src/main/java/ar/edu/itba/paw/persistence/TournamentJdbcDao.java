package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.ParticipantUser;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.Match;
import ar.edu.itba.paw.model.Pair;
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

    private static final RowMapper<Match> ROW_MAPPER_MATCH = (rs, rowNum) -> new Match(
        rs.getLong("id"),
        rs.getLong("tournament_id"),
        rs.getObject("local_id") != null ? rs.getLong("local_id") : null,
        rs.getObject("visitor_id") != null ? rs.getLong("visitor_id") : null,
        rs.getObject("local_score") != null ? rs.getInt("local_score") : null,
        rs.getObject("visitor_score") != null ? rs.getInt("visitor_score") : null,
        rs.getObject("winner") != null ? rs.getInt("winner") : null,
        rs.getObject("stage") != null ? rs.getInt("stage") : null
    );


    @Override
    public Optional<Tournament> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM tournament WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tournament t WHERE open_inscriptions = true");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filter.getName() != null){
            sql.append(" AND t.name LIKE :name");
            params.addValue("name", filter.getName());
        }if (filter.getGame_id() != null){
            sql.append(" AND t.game_id = :game_id");
            params.addValue("game_id", filter.getGame_id());
        }if (filter.getElo() != null){
            sql.append(" AND t.elo = :elo");
            params.addValue("elo", filter.getElo(), Types.OTHER);
        }if (filter.getRegion() != null){
            sql.append(" AND t.region = :region");
            params.addValue("region", filter.getRegion(), Types.OTHER);
        }if (filter.getFormat() != null){
            sql.append(" AND t.format = :format");
            params.addValue("format", filter.getFormat());
        }if (filter.getStructure() != null){
            sql.append(" AND t.structure = :structure");
            params.addValue("structure", filter.getStructure(), Types.OTHER);
        }if (filter.getStart_date() != null){
            sql.append(" AND t.start_date < :start_date");
            params.addValue("start_date", filter.getStart_date());
        }if (filter.getEnd_date() != null){
            sql.append(" AND t.end_date < :end_date");
            params.addValue("end_date", filter.getEnd_date());
        }

        return namedJdbcTemplate.query(sql.toString(), params, ROW_MAPPER);
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
//
//    public Integer getTournamentMaxParticipants(Long tournament_id) {
//		Tournament t = findById(tournament_id).orElse(null);
//		if (t != null) {
//
//		}
//    }

    @Override
    public void createMatches(Long tournamentId) {
        Tournament t = findById(tournamentId).orElse(null);
        List<ParticipantUser> participants = getTournamentParticipantUsers(tournamentId);

        if (t != null && !participants.isEmpty()) {
            int n = participants.size();

            // Odd # of participants -> add fictional participant
            if (n % 2 != 0) {
                participants.add(null);
                n++;
            }

            int rounds = n - 1;
            int halfSize = n / 2;

            List<ParticipantUser> rotated = new ArrayList<>(participants);
            int matchId = 1;

            for (int round = 0; round < rounds; round++) {
                for (int i = 0; i < halfSize; i++) {
                    ParticipantUser home = rotated.get(i);
                    ParticipantUser away = rotated.get(n - 1 - i);

                    if (home != null && away != null) {
                        Map<String, Object> values = new HashMap<>();
                        values.put("id", matchId++);
                        values.put("tournament_id", tournamentId);
                        values.put("local_id", home.getUser_id());
                        values.put("visitor_id", away.getUser_id());
                        values.put("local_score", null);
                        values.put("visitor_score", null);
                        values.put("winner", null);
                        values.put("stage", round + 1);
                        jdbcInsertMatch.execute(values);
                    }
                }
                ParticipantUser first = rotated.remove(1);
                rotated.add(first);
            }
        }
    }

    @Override
    public List<Match> getTournamentMatches(Long tournament_id) {
    	return jdbcTemplate.query("SELECT * FROM match WHERE tournament_id = ?", ROW_MAPPER_MATCH, tournament_id);
    }

    @Override
    public List<MatchWithPlayers> getTournamentMatchesWithPlayers(Long tournament_id) {
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
    public List<TournamentImg> findWithImg(TournamentFilter filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, i.image " +
                        "FROM tournament t " +
                        "LEFT JOIN image i ON t.image_id = i.id " +
                        "WHERE open_inscriptions = true"
        );
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filter.getName() != null){
            sql.append(" AND t.name LIKE = :name");
            params.addValue("name", filter.getName());
        }
        if (filter.getGame_id() != null){
            sql.append(" AND t.game_id = :game_id");
            params.addValue("game_id", filter.getGame_id());
        }
        if (filter.getElo() != null){
            sql.append(" AND t.elo = :elo");
            params.addValue("elo", filter.getElo(), Types.OTHER);
        }if (filter.getRegion() != null){
            sql.append(" AND t.region = :region");
            params.addValue("region", filter.getRegion(), Types.OTHER);
        }
        if (filter.getFormat() != null){
            sql.append(" AND t.format = :format");
            params.addValue("format", filter.getFormat());
        }
        if (filter.getStructure() != null){
            sql.append(" AND t.structure = :structure");
            params.addValue("structure", filter.getStructure(), Types.OTHER);
        }
        if (filter.getStart_date() != null){
            sql.append(" AND t.start_date < :start_date");
            params.addValue("start_date", filter.getStart_date());
        }
        if (filter.getEnd_date() != null){
            sql.append(" AND t.end_date < :end_date");
            params.addValue("end_date", filter.getEnd_date());
        }

        return namedJdbcTemplate.query(sql.toString(), params, ROW_MAPPER_IMG);
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

    @Override
    public List<Pair<String,String>> getGenericMatches(Long tournament_id) {
    	Structure structure = getTournamentStructure(tournament_id).orElse(null);
    	if (structure == null) return List.of();
    	int maxParticipants = jdbcTemplate.queryForObject("SELECT max_participants FROM tournament WHERE id = ?", Integer.class, tournament_id);
    	return structure.buildMatches(maxParticipants);
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

        jdbcTemplate.update("UPDATE participant_user SET points = points + 3 WHERE user_id = ?", winnerId);

        Integer totalMatches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM match WHERE tournament_id = ?", 
                Integer.class, tournamentId
        );
        
        Integer finishedMatches = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM match WHERE tournament_id = ? AND winner IS NOT NULL AND winner > 0", 
                Integer.class, tournamentId
        );
        
        if (totalMatches.equals(finishedMatches) && totalMatches > 0) {
            setFinished(tournamentId);
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
}
