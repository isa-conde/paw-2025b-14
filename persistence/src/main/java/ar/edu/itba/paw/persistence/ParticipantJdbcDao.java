package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ParticipantDao;
import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.Participant;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ParticipantJdbcDao implements ParticipantDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public ParticipantJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant")
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Participant> ROW_MAPPER_USER = (rs, rowNum) -> new Participant(
            rs.getLong("user_id"),
            rs.getString("username"),
            rs.getInt("points"),
            rs.getInt("group_number"),
            rs.getLong("profile_picture_id")
    );

    private static final RowMapper<Participant> ROW_MAPPER_TEAM = (rs, rowNum) -> new Participant(
            rs.getLong("team_id"),
            rs.getString("name"),
            rs.getInt("points"),
            rs.getInt("group_number"),
            rs.getLong("profile_picture_id")
    );

    @Override
    public List<Participant> getTournamentParticipantUsers(Long tournament_id) {

        return jdbcTemplate.query("SELECT * FROM participant " +
                                      "INNER JOIN users ON participant.user_id = users.id " +
                                      "WHERE tournament_id = ?", ROW_MAPPER_USER, tournament_id);
    }

    @Override
    public List<Participant> getTournamentParticipantTeams(Long tournament_id) {
        return jdbcTemplate.query("SELECT * FROM participant " +
                "INNER JOIN team ON participant.team_id = team.id " +
                "WHERE tournament_id = ? AND user_id IS NULL", ROW_MAPPER_TEAM, tournament_id);
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
        Map<String, Object> values = new HashMap<>();

        values.put("user_id", user_id);
        values.put("tournament_id", tournament_id);
        values.put("points", 0);

        jdbcInsert.execute(values);
    }

    @Override
    public void joinTournamentUserWithTeam(Long user_id, Long tournament_id, Long team_id) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", user_id);
        values.put("tournament_id", tournament_id);
        values.put("team_id", team_id);
        values.put("points", 0);
        jdbcInsert.execute(values);
    }

    @Override
    public void joinTournamentTeam(Long tournamentId, Long teamId) {
        Map<String, Object> values = new HashMap<>();
        values.put("team_id", teamId);
        values.put("tournament_id", tournamentId);
        values.put("points", 0);
        jdbcInsert.execute(values);
    }

    @Override
    public Participant getTournamentParticipantByUserId(Long tournament_id, Long user_id) {
        return jdbcTemplate.query("SELECT * FROM participant " +
                        "INNER JOIN users ON participant.user_id = users.id " +
                        "WHERE tournament_id = ? AND user_id = ?",
                                       ROW_MAPPER_USER, tournament_id, user_id).stream().findFirst().orElse(null);
    }

    @Override
    public Boolean hasJoined(Long userId, Long tournamentId) {
        String sql = "SELECT 1 FROM participant WHERE user_id = ? AND tournament_id = ? LIMIT 1";
        return !jdbcTemplate.query(sql, (rs, rowNum) -> 1, userId, tournamentId).isEmpty();
    }

    @Override
    public void leaveTournamentUser(Long user_id, Long tournament_id) {
        jdbcTemplate.update("DELETE FROM participant WHERE user_id = ? AND tournament_id = ?", user_id, tournament_id);
    }

    @Override
    public void leaveTournamentTeam(Long userId, Long tournamentId) {
        final String sql = """
            DELETE FROM participant p
            USING participant pu
            WHERE pu.user_id = ? 
              AND pu.tournament_id = ?
              AND pu.team_id IS NOT NULL
              AND p.tournament_id = pu.tournament_id
              AND p.team_id = pu.team_id
        """;
        jdbcTemplate.update(sql, userId, tournamentId);
    }

    @Override
    public void updateGroupNumberForUsers(long tournamentId, int groupNumber, List<Long> userIds, Integer teamSize) {
        String idColumn = (teamSize != null && teamSize > 1) ? "team_id" : "user_id";

        String sql = "UPDATE participant " +
                "SET group_number = :groupNumber " +
                "WHERE tournament_id = :tournamentId AND " + idColumn + " IN (:ids)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tournamentId", tournamentId)
                .addValue("groupNumber", groupNumber)
                .addValue("ids", userIds);

        namedJdbcTemplate.update(sql, params);
    }

    @Override
    public void swapGroups(Long tournament_id, Long user1, Long user2, Integer group1, Integer group2, Integer teamSize){
        jdbcTemplate.update(
                "UPDATE participant " +
                        "SET group_number = CASE " +
                        "  WHEN user_id = ? THEN ? " +
                        "  WHEN user_id = ? THEN ? " +
                        "  ELSE group_number END " +
                        "WHERE tournament_id = ? AND user_id IN (?, ?)",
                user1, group2,
                user2, group1,
                tournament_id, user1, user2
        );
    }

    @Override
    public Integer getTournamentMaxPointsGroup(Long tournamentId, Integer group){
        return jdbcTemplate.queryForObject(
                "SELECT MAX(points) FROM participant WHERE tournament_id = ? AND group_number = ?",
                Integer.class, tournamentId, group
        );
    }

    @Override
    public Integer getTournamentSecondMaxPointsGroup(Long tournamentId, Integer group) {
        return jdbcTemplate.queryForObject(
                "SELECT MAX(points) FROM participant WHERE tournament_id = ? AND points < ? AND group_number = ?",
                Integer.class, tournamentId, getTournamentMaxPointsGroup(tournamentId, group), group
        );
    }

    @Override
    public Integer getGroupNumber(Long tournamentId, Long userId, Integer teamSize) {
        String sql =
                "  SELECT group_number FROM participant " +
                "  WHERE tournament_id = :tournamentId AND " +
                (teamSize != null && teamSize > 1 ? "team_id = :id " : "user_id = :id ") +
                "  LIMIT 1";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tournamentId", tournamentId)
                .addValue("id", userId);
        Integer ans = namedJdbcTemplate.queryForObject(sql, params, Integer.class);
        if(ans == null){
            return 0;
        }
        return ans;
    }

    @Override
    public List<Participant> getTournamentParticipantsByPoints(
            Long tournamentId, Integer groupNumber, Integer points, Integer teamSize) {

        if (teamSize > 1) {
            return getTournamentParticipantsByPointsTeam(tournamentId, groupNumber, points);
        } else {
            return getTournamentParticipantsByPointsUser(tournamentId, groupNumber, points);
        }
    }


    private List<Participant> getTournamentParticipantsByPointsUser(Long tournamentId, Integer groupNumber, Integer points) {

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM participant p " +
                        "INNER JOIN users u ON p.user_id = u.id " +
                        "WHERE p.tournament_id = :tournamentId AND p.points = :points"
        );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tournamentId", tournamentId)
                .addValue("points", points);

        if (groupNumber != null) {
            sql.append(" AND p.group_number = :groupNumber");
            params.addValue("groupNumber", groupNumber);
        }

        return namedJdbcTemplate.query(sql.toString(), params, ROW_MAPPER_USER);
    }

    private List<Participant> getTournamentParticipantsByPointsTeam(Long tournamentId, Integer groupNumber, Integer points) {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM participant p " +
                        "INNER JOIN team t ON p.team_id = t.id " +
                        "WHERE p.tournament_id = :tournamentId AND p.points = :points"
        );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tournamentId", tournamentId)
                .addValue("points", points);

        if (groupNumber != null) {
            sql.append(" AND p.group_number = :groupNumber");
            params.addValue("groupNumber", groupNumber);
        }

        return namedJdbcTemplate.query(sql.toString(), params, ROW_MAPPER_TEAM);
    }

    @Override
    public Integer getTournamentGroups(Long tournamentId){
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT group_number) FROM participant WHERE tournament_id = ?",
                Integer.class, tournamentId
        );
    }

    @Override
    public void sumPoints(Long tournamentId, Long userId, Integer points){
        jdbcTemplate.update("UPDATE participant SET points = points + ? WHERE user_id = ?", points, userId);
    }
}
